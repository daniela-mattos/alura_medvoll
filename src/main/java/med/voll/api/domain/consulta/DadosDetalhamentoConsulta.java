package med.voll.api.domain.consulta;

import java.time.LocalDate;

public record DadosDetalhamentoConsulta(Long idConsulta,
                                        Long idMedico,
                                        Long idPaciente,
                                        LocalDate data
                                        ) {

}
