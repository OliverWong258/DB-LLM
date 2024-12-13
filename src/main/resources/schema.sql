CREATE TABLE IF NOT EXISTS Policies (
        id INT AUTO_INCREMENT PRIMARY KEY,
        type VARCHAR(50),
        date DATE,
        day_of_the_week VARCHAR(20),
        agency VARCHAR(255),
        subagency VARCHAR(255),
        subject JSON,
        chineseSubject VARCHAR(255),
        cfr VARCHAR(255),
        depdoc VARCHAR(1023),
        frdoc VARCHAR(255),
        bilcod VARCHAR(255),
        summary TEXT,
        chinese_summary TEXT,
        content LONGTEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;