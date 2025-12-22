# INSERT INTO students (name, grade, school) VALUES
# ('Alice Johnson', '9th Grade', 'Springfield High School'),
# ('Bob Smith', '10th Grade', 'Riverside Academy'),
# ('Charlie Brown', '11th Grade', 'Lincoln High School'),
# ('Diana Prince', '12th Grade', 'Westfield School'),
# ('Ethan Hunt', '9th Grade', 'Central High School');
#
# INSERT INTO orders (student_id, total, status) VALUES
# (1, 25.50, 'paid'),
# (1, 12.75, 'pending'),
# (2, 30.00, 'paid'),
# (3, 18.25, 'pending'),
# (4, 45.00, 'paid'),
# (5, 22.50, 'pending');

-- Only insert sample data if tables are empty
INSERT INTO students (name, grade, school)
SELECT * FROM (
                  SELECT 'Alice Johnson', '9th Grade', 'Springfield High School' UNION ALL
                  SELECT 'Bob Smith', '10th Grade', 'Riverside Academy' UNION ALL
                  SELECT 'Charlie Brown', '11th Grade', 'Lincoln High School' UNION ALL
                  SELECT 'Diana Prince', '12th Grade', 'Westfield School' UNION ALL
                  SELECT 'Ethan Hunt', '9th Grade', 'Central High School'
              ) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM students);

INSERT INTO orders (student_id, total, status)
SELECT * FROM (
                  SELECT 1, 25.50, 'paid' UNION ALL
                  SELECT 1, 12.75, 'pending' UNION ALL
                  SELECT 2, 30.00, 'paid' UNION ALL
                  SELECT 3, 18.25, 'pending' UNION ALL
                  SELECT 4, 45.00, 'paid' UNION ALL
                  SELECT 5, 22.50, 'pending'
              ) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM orders);
