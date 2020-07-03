package org.refactoringminer;

import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BatchMinerToSQL {

    public static String DBUri = "jdbc:mysql://localhost:3306/satd?useSSL=false";

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Scanner reader = new Scanner(new File("maldonado_study.csv"));
        while( reader.hasNextLine() ) {
            String line = reader.nextLine();
            final String repo = line.split(",")[0];
            final String repoName = getFilePath(repo);
            Repository repoRef = gitService.cloneIfNotExists("tmp/" + repoName, repo);

            miner.detectAll(repoRef, "master", new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    if( refactorings.isEmpty() ) {
                        return;
                    }
                    try {
                        Connection conn = DriverManager.getConnection(DBUri, "root", "root");
                        refactorings.forEach( ref -> {
                            final String refType = ref.getRefactoringType().name();
                            writeRefactoring(conn, repoName, commitId, refType, new Date(),
                                    getAllRefsBefore(ref), getAllRefsAfter(ref));
                        });
                    } catch (Exception e) {
                        System.err.println("UH OH!");
                    }
                }
            });
        }
    }

    public static void writeRefactoring(Connection dbConn,
                                        String pName, String commitHash, String refType, Date commitDate,
                                        List<RefInFile> before, List<RefInFile> after) {
        try {
            PreparedStatement stmt = dbConn.prepareStatement(
                    "INSERT INTO satd.Refactoring(p_name,commit_hash,ref_type,ref_date) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, pName);
            stmt.setString(2, commitHash);
            stmt.setString(3, refType);
            stmt.setTimestamp(4, new Timestamp(commitDate.getTime()));
            stmt.executeUpdate();
            ResultSet result = stmt.getGeneratedKeys();
            if( result.next() ) {
                int refId = result.getInt(1);
                before.forEach(fileRef -> writeFileRef(dbConn, refId, fileRef, true));
                after.forEach(fileRef -> writeFileRef(dbConn, refId, fileRef, false));
            }
        } catch (Exception e) {
            System.err.println("SQL Oopsie");
            e.printStackTrace();
        }
    }

    public static void writeFileRef(Connection dbConn, int refId, RefInFile fileRef, boolean isBefore) {
        try {
            PreparedStatement stmt = dbConn.prepareStatement(
                    "INSERT INTO satd.RefactoringInFile(ref_id, file_path, class, start_line, end_line, is_before) " +
                            "VALUES (?,?,?,?,?,?)");
            stmt.setInt(1, refId);
            stmt.setString(2, fileRef.path);
            stmt.setString(3, fileRef.clazz);
            stmt.setInt(4, fileRef.startLine);
            stmt.setInt(5, fileRef.endLine);
            stmt.setBoolean(6, isBefore);
            stmt.execute();
        } catch (Exception e) {
            System.err.println("SQL2 Oopsie");
            e.printStackTrace();
        }
    }

    public static List<RefInFile> getAllRefsBefore(Refactoring ref) {
        return ref.getInvolvedClassesBeforeRefactoring().stream()
                .flatMap(pair ->
                        ref.leftSide().stream()
                                .filter(range -> range.getFilePath().equals(pair.left))
                                .map(range -> new RefInFile(pair.left, pair.right, range.getStartLine(), range.getEndLine()))
                ).collect(Collectors.toList());
    }

    public static List<RefInFile> getAllRefsAfter(Refactoring ref) {
        return ref.getInvolvedClassesAfterRefactoring().stream()
                .flatMap(pair ->
                        ref.rightSide().stream()
                                .filter(range -> range.getFilePath().equals(pair.left))
                                .map(range -> new RefInFile(pair.left, pair.right, range.getStartLine(), range.getEndLine()))
                ).collect(Collectors.toList());
    }

    public static String getFilePath(String githubURL) {
        String[] slashParse = githubURL.split("/");
        return (slashParse[slashParse.length - 2] + "/" +  slashParse[slashParse.length - 1])
                .replace(".git", "");
    }

    static class RefInFile {
        public String path, clazz;
        public int startLine, endLine;
        public RefInFile(String path, String clazz, int startLine, int endLine) {
            this.path = path;
            this.clazz = clazz;
            this.startLine = startLine;
            this.endLine = endLine;
        }
    }
}
