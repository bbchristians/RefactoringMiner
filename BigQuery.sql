BEGIN;    
	-- Constants
	SET @project_name = "apache/camel";
    Set @instance_id = "-1";

	-- Query
	SELECT 
        SATD.satd_instance_id, SATD.resolution,
			FirstFile.f_path as v1_path, 
			FirstFile.containing_class as v1_class, 
		SecondCommit.commit_hash as v2_commit, 
			SecondFile.f_path as v2_path, 
			SecondFile.containing_class as v2_class, 
		Refactoring.ref_type,
        -- RefactoringInFile.start_line, RefactoringInFile.end_line,
        RefactoringInFile.is_before,
        RefactoringInFile.class
        -- IF(RefactoringInFile.is_before, FirstFile.start_line, SecondFile.start_line) as start_line,
        -- IF(RefactoringInFile.is_before, FirstFile.end_line, SecondFile.end_line) as end_line
		FROM satd.SATD
		INNER JOIN satd.SATDInFile as FirstFile
			ON SATD.first_file = FirstFile.f_id
		INNER JOIN satd.SATDInFile as SecondFile
			ON SATD.second_file = SecondFile.f_id
		INNER JOIN satd.Commits as FirstCommit
			ON SATD.first_commit = FirstCommit.commit_hash 
				AND SATD.p_id = FirstCommit.p_id
		INNER JOIN satd.Commits as SecondCommit
			ON SATD.second_commit = SecondCommit.commit_hash
				AND SATD.p_id = SecondCommit.p_id
        INNER JOIN satd.Projects
			ON SATD.p_id=Projects.p_id
		INNER JOIN satd.Refactoring
			ON Refactoring.p_name=Projects.p_name
				AND SecondCommit.commit_hash=Refactoring.commit_hash
		INNER JOIN satd.RefactoringInFile
			ON RefactoringInFile.ref_id=Refactoring.ref_id
				AND IF(RefactoringInFile.is_before, 
						RefactoringInFile.file_path=FirstFile.f_path,
                        RefactoringInFile.file_path=SecondFile.f_path)
				AND IF(RefactoringInFile.is_before, 
 						RefactoringInFile.class=FirstFile.containing_class,
						RefactoringInFile.class=SecondFile.containing_class)
				AND (RefactoringInFile.method="None" OR
					IF(RefactoringInFile.is_before,
						RefactoringInFile.method=FirstFile.containing_method,
                        RefactoringInFile.method=SecondFile.containing_method))
		-- WHERE 
			-- IF(RefactoringInFile.is_before,
--             -- Refactoring details modifications to the first version of the file
-- 				GREATEST(RefactoringInFile.start_line, FirstFile.start_line) 
--                 <= LEAST(RefactoringInFile.end_line  , FirstFile.end_line)
--             ,
--             -- Refactoring details modifications to the second version of the file
-- 				GREATEST(RefactoringInFile.start_line, SecondFile.start_line) 
--                 <= LEAST(RefactoringInFile.end_line  , SecondFile.end_line)
--             )
		-- WHERE Projects.p_name=@project_name 
        -- AND SecondCommit.commit_hash="849ae58cfb2d68bf8f6c7a5ee6598fc7363a4b67"
        -- WHERE SATD.satd_instance_id=@instance_id
        ORDER BY satd_id DESC;
    
