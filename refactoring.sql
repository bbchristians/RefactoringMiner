drop table if exists satd.Refactoring, satd.RefactoringInFile;

CREATE TABLE IF NOT EXISTS satd.Refactoring (
	ref_id INT UNIQUE auto_increment,
	p_name varchar(256),
    commit_hash varchar(256),
    ref_type varchar(256),
    ref_date TIMESTAMP,
    PRIMARY KEY (ref_id)
);

CREATE TABLE IF NOT EXISTS satd.RefactoringInFile (
	ref_id INT,
    file_path varchar(4096),
    class varchar(512),
    method varchar(512),
    start_line int,
    end_line int,
    is_before bool,
    FOREIGN KEY (ref_id) REFERENCES Refactoring(ref_id)
);

