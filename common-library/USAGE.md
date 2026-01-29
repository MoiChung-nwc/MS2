# Hướng dẫn sử dụng Common Library

## Bước 1: Build Common Library

```bash
cd d:\Savvy\micro\common-library
mvn clean install
```

## Bước 2: Thêm dependency vào các service

Trong file `pom.xml` của mỗi service (auth-service, student-service, grade-service), thêm:

```xml
<dependency>
    <groupId>com.savvy</groupId>
    <artifactId>common-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Bước 3: Sử dụng trong code

### Controller Example

```java
package com.savvy.student.controller;

import com.savvy.common.dto.BaseResponse;
import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvy.common.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Student>> getStudent(@PathVariable Long id) {
        Student student = studentService.findById(id)
            .orElseThrow(() -> new BusinessException(
                ErrorCode.STUDENT_NOT_FOUND,
                "Student with ID " + id + " not found"
            ));

        return ResponseUtil.ok(student, "Student retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Student>> createStudent(
            @RequestBody @Valid StudentDTO dto) {
        Student student = studentService.create(dto);
        return ResponseUtil.created(student, "Student created successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseUtil.noContent();
    }
}
```

### Service Example

```java
package com.savvy.student.service;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    public Student findById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                ErrorCode.STUDENT_NOT_FOUND,
                "Student not found with id: " + id
            ));
    }

    public Student create(StudentDTO dto) {
        // Check if student already exists
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(
                ErrorCode.STUDENT_ALREADY_EXISTS,
                "Student with email " + dto.getEmail() + " already exists"
            );
        }

        Student student = new Student();
        // ... map dto to entity
        return studentRepository.save(student);
    }
}
```

## Các tính năng chính

### 1. Standardized Response Format

Tất cả API response đều có format nhất quán:

```json
{
  "success": true,
  "status": 200,
  "message": "Success message",
  "data": { ... },
  "timestamp": "2026-01-22T10:30:00"
}
```

### 2. Error Response Format

```json
{
	"success": false,
	"status": 404,
	"message": "Student not found",
	"error": {
		"code": "3001",
		"message": "Student not found with id: 123",
		"details": "..."
	},
	"timestamp": "2026-01-22T10:30:00"
}
```

### 3. Automatic Exception Handling

`GlobalExceptionHandler` tự động xử lý các exception:

-    `BusinessException` → Custom error response
-    `MethodArgumentNotValidException` → Validation errors
-    `Exception` → Generic error response

### 4. Error Codes

Mã lỗi được tổ chức theo service:

-    **1xxx**: Common errors
-    **2xxx**: Authentication (auth-service)
-    **3xxx**: Students (student-service)
-    **4xxx**: Grades (grade-service)
-    **5xxx**: Business logic
-    **6xxx**: External services

## Build các service với Common Library

Sau khi build common library, build từng service:

```bash
# Build auth-service
cd d:\Savvy\micro\auth-service
mvn clean install

# Build student-service
cd d:\Savvy\micro\student-service
mvn clean install

# Build grade-service
cd d:\Savvy\micro\grade-service
mvn clean install
```

## Troubleshooting

Nếu gặp lỗi "Cannot resolve dependency", hãy:

1. Đảm bảo common-library đã được build thành công
2. Kiểm tra file `~/.m2/repository/com/savvy/common-library/1.0.0/`
3. Run `mvn clean install` lại trong common-library
