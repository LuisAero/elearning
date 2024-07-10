package com.imsoftware.students.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.imsoftware.students.repository.StudentRepository;
import org.springframework.stereotype.Service;

import com.imsoftware.students.domain.StudentDTO;
import com.imsoftware.students.entity.Student;
import com.imsoftware.students.service.IStudentService;

@Service
public class StudentServiceImpl implements IStudentService {

	private final StudentRepository studentRepository;

	public StudentServiceImpl(StudentRepository studentRepository) {
		super();
		this.studentRepository = studentRepository;
	}

	@Override
	public Collection<StudentDTO> findAll() {
		return studentRepository.findAll().stream().map(new Function<Student, StudentDTO>() {
			@Override
			public StudentDTO apply(Student student) {
				List<String> programmingLanguagesKnowAbout = student.getSubjects().stream()
						.map(pl -> new String(pl.getName())).collect(Collectors.toList());
				return new StudentDTO(student.getName(), programmingLanguagesKnowAbout);
			}

		}).collect(Collectors.toList());
		
	}

	@Override
	public Collection<StudentDTO> findAllAndShowIfHaveAPopularSubject() {
		// TODO Obtener la lista de todos los estudiantes e indicar la materia más concurrida existentes en la BD e
		// indicar si el estudiante cursa o no la materia más concurrida registrado en la BD.

		HashMap<String, Integer> count = new HashMap<>();
		return studentRepository.findAll().stream().map(new Function<Student, StudentDTO>() {
			@Override
			public StudentDTO apply(Student student) {
				List<String> programmingLanguagesKnowAbout = student.getSubjects().stream()
						.map(pl -> {
							if(count.containsKey(pl.getName()))
								count.put(pl.getName(), count.get(pl.getName()) + 1);
							else
								count.put(pl.getName(), 1);
							return new String(pl.getName());
						}).collect(Collectors.toList());
				return new StudentDTO(student.getName(), programmingLanguagesKnowAbout);
			}
		}).map(student -> {
			Boolean hasPopularSubject = student.getCurrentSubject().stream()
					.anyMatch(subject -> count.get(subject) == Collections.max(count.values()));
			List<String> popularSubject = student.getCurrentSubject().stream()
					.filter(subject -> count.get(subject) == Collections.max(count.values())).collect(Collectors.toList());
			return new StudentDTO(student.getStudentName(), popularSubject, hasPopularSubject);
		}).collect(Collectors.toList());
	}

}
