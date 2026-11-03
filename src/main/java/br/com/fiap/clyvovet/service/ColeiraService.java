package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.LeituraColeiraRequest;
import br.com.fiap.clyvovet.dto.response.LeituraColeiraResponse;
import br.com.fiap.clyvovet.mapper.LeituraColeiraMapper;
import br.com.fiap.clyvovet.model.Endereco;
import br.com.fiap.clyvovet.model.LeituraColeira;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.repository.EnderecoRepository;
import br.com.fiap.clyvovet.repository.LeituraColeiraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ColeiraService {

    /** Posição usada quando o tutor não tem endereço com coordenadas (Av. Paulista, São Paulo). */
    static final BigDecimal LATITUDE_PADRAO = new BigDecimal("-23.56140000");
    static final BigDecimal LONGITUDE_PADRAO = new BigDecimal("-46.65580000");

    private final LeituraColeiraRepository leituraColeiraRepository;
    private final EnderecoRepository enderecoRepository;
    private final PetService petService;
    private final NotificacaoService notificacaoService;
    private final LeituraColeiraMapper leituraColeiraMapper;
    private final Clock relogio;

    public ColeiraService(LeituraColeiraRepository leituraColeiraRepository, EnderecoRepository enderecoRepository,
                          PetService petService, NotificacaoService notificacaoService,
                          LeituraColeiraMapper leituraColeiraMapper, Clock relogio) {
        this.leituraColeiraRepository = leituraColeiraRepository;
        this.enderecoRepository = enderecoRepository;
        this.petService = petService;
        this.notificacaoService = notificacaoService;
        this.leituraColeiraMapper = leituraColeiraMapper;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public List<LeituraColeiraResponse> ultimasLeituras(Long petId) {
        return leituraColeiraRepository.findTop10ByPet_IdOrderByDataHoraDesc(petId).stream()
                .map(leituraColeiraMapper::paraResponse)
                .toList();
    }

    /** Simula o envio de uma leitura pela coleira. No Oracle, a gravação dispara o trigger de auditoria. */
    public LeituraColeiraResponse registrarLeitura(LeituraColeiraRequest request) {
        Pet pet = petService.buscar(request.getPetId());
        Optional<Endereco> enderecoComCoordenadas = enderecoRepository
                .findFirstByUsuario_IdOrderByIdAsc(pet.getTutor().getUsuario().getId())
                .filter(endereco -> endereco.getLatitude() != null && endereco.getLongitude() != null);
        BigDecimal latitude = enderecoComCoordenadas.map(Endereco::getLatitude).orElse(LATITUDE_PADRAO);
        BigDecimal longitude = enderecoComCoordenadas.map(Endereco::getLongitude).orElse(LONGITUDE_PADRAO);

        LeituraColeira leitura = leituraColeiraRepository.save(new LeituraColeira(pet, LocalDateTime.now(relogio),
                latitude, longitude, request.getBatimentoBpm()));
        if (leitura.isAlerta()) {
            notificacaoService.notificar(pet.getTutor().getUsuario(),
                    "Alerta IoT: a coleira de %s registrou %s bpm, fora da faixa normal."
                            .formatted(pet.getNome(), leitura.getBatimentoBpm().toPlainString()));
        }
        return leituraColeiraMapper.paraResponse(leitura);
    }
}
