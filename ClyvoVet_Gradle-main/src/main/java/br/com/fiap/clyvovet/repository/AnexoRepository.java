package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Anexo;
import br.com.fiap.clyvovet.model.TipoArquivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnexoRepository extends JpaRepository<Anexo, Integer> {
    Page<Anexo> findByTipoArquivo(TipoArquivo tipoArquivo, Pageable pageable);
}
