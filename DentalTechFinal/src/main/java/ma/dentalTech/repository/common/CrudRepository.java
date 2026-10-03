

package ma.dentalTech.repository.common;

import ma.dentalTech.entities.ordonnance.Ordonnance;

import java.util.List;

public interface CrudRepository<T, ID> {

    List<T> findAll();

    T findById(ID id);

    void create(T newElement);

    void update(T newValuesElement);

    void delete(T patient);

    void deleteById(ID id);

    //List<Ordonnance> findByPatientId(Long idPatient);
}



