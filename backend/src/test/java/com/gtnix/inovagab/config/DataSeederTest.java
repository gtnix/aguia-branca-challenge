package com.gtnix.inovagab.config;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private OrientacaoRepository orientacaoRepository;
    @Mock
    private IdeiaRepository ideiaRepository;
    @Mock
    private ProjetoRepository projetoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private DataSeeder seeder;

    @BeforeEach
    void setUp() {
        seeder = new DataSeeder(
                usuarioRepository,
                orientacaoRepository,
                ideiaRepository,
                projetoRepository,
                passwordEncoder
        );
    }

    @Test
    void normalizaCategoriaOperacoesEmDocumentoExistente() {
        when(usuarioRepository.count()).thenReturn(1L);
        when(ideiaRepository.findAll()).thenReturn(List.of());
        when(projetoRepository.findAll()).thenReturn(List.of());
        OrientacaoDocument legado = orientacaoLegada();
        when(orientacaoRepository.findAll()).thenReturn(List.of(legado));
        stubUpsertCatalogo();

        seeder.run();

        assertEquals(DataSeeder.CATEGORIA_DEMO, legado.getCategoria());
        verify(orientacaoRepository).save(legado);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void removeIdeiaDeTesteESemeiaCatalogoCanonicamente() {
        when(usuarioRepository.count()).thenReturn(1L);
        IdeiaDocument lixo = new IdeiaDocument();
        lixo.setId("ideia-lixo");
        lixo.setTitulo("Auditoria fatia 3");
        lixo.setDescricao("Prova CRUD remoto com vinculo.");
        lixo.setAtivo(true);
        when(ideiaRepository.findAll()).thenReturn(List.of(lixo));
        when(projetoRepository.findAll()).thenReturn(List.of());
        when(orientacaoRepository.findAll()).thenReturn(List.of());
        stubUpsertCatalogo();

        seeder.run();

        assertFalse(lixo.isAtivo());
        verify(ideiaRepository).save(lixo);
        verify(orientacaoRepository, atLeastOnce()).save(any(OrientacaoDocument.class));
        verify(ideiaRepository, atLeastOnce()).save(any(IdeiaDocument.class));
        verify(projetoRepository, atLeastOnce()).save(any());
    }

    @Test
    void detectaConteudoDeTestePorPalavraChave() {
        assertTrue(DataSeeder.isConteudoTeste("Ideia alheia smoke", "ownership"));
        assertFalse(DataSeeder.isConteudoTeste("Rastreamento de frota em tempo real", "Telemetria"));
    }

    private OrientacaoDocument orientacaoLegada() {
        OrientacaoDocument legado = new OrientacaoDocument();
        legado.setId("ori-legado");
        legado.setCategoria("Operações");
        legado.setAtivo(true);
        return legado;
    }

    private void stubUpsertCatalogo() {
        when(orientacaoRepository.findById(any())).thenReturn(Optional.empty());
        when(ideiaRepository.findById(any())).thenReturn(Optional.empty());
        when(projetoRepository.findById(any())).thenReturn(Optional.empty());
        when(orientacaoRepository.save(any(OrientacaoDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(ideiaRepository.save(any(IdeiaDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(projetoRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
    }
}
