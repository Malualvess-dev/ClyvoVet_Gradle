package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Anexo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnexoRepository extends JpaRepository<Anexo, Long> {

    List<Anexo> findByUsuario_IdOrderByDataCriacaoDesc(Long usuarioId);

    Optional<Anexo> findByIdAndUsuario_Id(Long anexoId, Long usuarioId);
}
