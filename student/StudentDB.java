package info.kgeorgiy.ja.yakupova.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentQuery {


    private final Comparator<Student> STUDENT_COMPARATOR = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName).reversed()
            .thenComparingInt(Student::getId);

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return toListByMap(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return toListByMap(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return toListByMap(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return toListByMap(students, s -> s.getFirstName() + " " + s.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        // :NOTE: you should not collect into an intermediary list before collecting to set
        // :NOTE-2: there is no guarantee on the ordering
        return makeMap(students, Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream()
                // :NOTE: use natural ordering on students instead of creating a new comparator
                // :NOTE: streams have a max method
                .max(Student::compareTo)
                // :NOTE-2: you could have just used a map
                .map(Student::getFirstName).orElse("");
//                .flatMap(o -> Optional.of(o.getFirstName())).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return students.stream()
                .sorted(Student::compareTo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        // :NOTE: please, move this comparator into a constant
        return toListByWithSort(students.stream());

    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        // also intermediaries
        return toListByFilter(students, s -> s.getFirstName().equals(name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return toListByFilter(students, s -> s.getLastName().equals(name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return toListByFilter(students, s -> s.getGroup().equals(group));
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return makeFilter(students, s -> s.getGroup().equals(group))
                .collect(Collectors.toMap(Student::getLastName,
                        Student::getFirstName,
                        (s1, s2) -> s1.compareTo(s2) < 0 ? s1 : s2));
    }

    private <R> Stream<R> makeMap(List<Student> students, Function<Student, R> func) {
        return students.stream().map(func);
    }

    // :NOTE: no need to return just a list of strings, you can generify it
    private <R> List<R> toListByMap(List<Student> students, Function<Student, R> func) {
        // :NOTE: there was something weird with generics
        return makeMap(students, func).collect(Collectors.toList());
    }

    private Stream<Student> makeFilter(Collection<Student> students, Predicate<Student> func) {
        return students.stream().filter(func);
    }
    private List<Student> toListByWithSort(Stream<Student> stream){
        return stream.sorted(STUDENT_COMPARATOR).collect(Collectors.toList());
    }

    private List<Student> toListByFilter(Collection<Student> students, Predicate<Student> func) {
        return toListByWithSort(makeFilter(students, func));
    }

}
