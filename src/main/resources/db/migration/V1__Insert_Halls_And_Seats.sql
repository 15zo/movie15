-- 1. Halls 데이터 삽입
INSERT INTO  movie15.hall (name, seat_count, created_at, modified_at) VALUES
                                                                 ('1관', 35, NOW(), NOW()),
                                                                 ('2관', 45, NOW(), NOW()),
                                                                 ('3관', 30, NOW(), NOW()),
                                                                 ('IMAX관', 50, NOW(), NOW()),
                                                                 ('4DX관', 40, NOW(), NOW());

-- 2. Seats 데이터 생성

-- 1관 35석
INSERT INTO movie15.seat (hall_id, row_num, col_num, type, status)
SELECT
    (SELECT id FROM movie15.hall WHERE name = '1관') AS hall_id,
    r.row_num,
    c.col_num,
    CASE WHEN r.row_num <= 2 THEN 'VIP' ELSE 'Economy' END AS type,
    TRUE AS status
FROM
    (SELECT 1 AS row_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) r,
    (SELECT 1 AS col_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7) c
WHERE
    (r.row_num - 1) * 7 + c.col_num <= 35;

-- 2관 45석
INSERT INTO movie15.seat (hall_id, row_num, col_num, type, status)
SELECT
    (SELECT id FROM movie15.hall WHERE name = '2관') AS hall_id,
    r.row_num,
    c.col_num,
    'Economy' AS type,
    TRUE AS status
FROM
    (SELECT 1 AS row_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6) r,
    (SELECT 1 AS col_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7) c
WHERE
    (r.row_num - 1) * 7 + c.col_num <= 45;

-- 3관 30석
INSERT INTO movie15.seat (hall_id, row_num, col_num, type, status)
SELECT
    (SELECT id FROM movie15.hall WHERE name = '3관') AS hall_id,
    r.row_num,
    c.col_num,
    CASE WHEN r.row_num = 1 THEN 'VIP' ELSE 'Economy' END AS type,
    TRUE AS status
FROM
    (SELECT 1 AS row_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) r,
    (SELECT 1 AS col_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6) c
WHERE
    (r.row_num - 1) * 6 + c.col_num <= 30;

-- IMAX관 50석
INSERT INTO movie15.seat (hall_id, row_num, col_num, type, status)
SELECT
    (SELECT id FROM movie15.hall WHERE name = 'IMAX관') AS hall_id,
    r.row_num,
    c.col_num,
    CASE WHEN r.row_num <= 2 THEN 'VIP' ELSE 'Economy' END AS type,
    TRUE AS status
FROM
    (SELECT 1 AS row_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) r,
    (SELECT 1 AS col_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) c
WHERE
    (r.row_num - 1) * 10 + c.col_num <= 50;

-- 4DX관 40석
INSERT INTO movie15.seat (hall_id, row_num, col_num, type, status)
SELECT
    (SELECT id FROM movie15.hall WHERE name = '4DX관') AS hall_id,
    r.row_num,
    c.col_num,
    CASE WHEN r.row_num = 1 THEN 'VIP' ELSE 'Economy' END AS type,
    TRUE AS status
FROM
    (SELECT 1 AS row_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) r,
    (SELECT 1 AS col_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8) c
WHERE
    (r.row_num - 1) * 8 + c.col_num <= 40;