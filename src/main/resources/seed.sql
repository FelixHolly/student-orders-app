-- Sample students
INSERT INTO students (name, grade, school) VALUES
('Alice Johnson', '9th Grade', 'Springfield High School'),
('Bob Smith', '10th Grade', 'Riverside Academy'),
('Charlie Brown', '11th Grade', 'Lincoln High School'),
('Diana Prince', '12th Grade', 'Westfield School'),
('Ethan Hunt', '9th Grade', 'Central High School');

-- Sample orders
INSERT INTO orders (student_id, total, status) VALUES
(1, 25.50, 'paid'),
(1, 12.75, 'pending'),
(2, 30.00, 'paid'),
(3, 18.25, 'pending'),
(4, 45.00, 'paid'),
(5, 22.50, 'pending');
