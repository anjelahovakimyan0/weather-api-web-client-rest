package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.Specialty;

import java.util.List;

public interface SpecialtyService {

    List<Specialty> findAll();

    List<Specialty> findById(int i);
}