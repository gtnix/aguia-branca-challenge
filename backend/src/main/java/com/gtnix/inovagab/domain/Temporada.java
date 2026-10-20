package com.gtnix.inovagab.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;

/**
 * Temporada trimestral do ranking ("2026-T4"). Derivada do calendário: não há cadastro
 * de temporadas, então todos os eventos de um trimestre caem na mesma temporada.
 */
public record Temporada(String id, String label, LocalDate inicio, LocalDate fim) {

    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    public static Temporada de(LocalDate data) {
        int trimestre = data.get(IsoFields.QUARTER_OF_YEAR);
        LocalDate inicio = LocalDate.of(data.getYear(), (trimestre - 1) * 3 + 1, 1);
        LocalDate fim = inicio.plusMonths(3).minusDays(1);
        return new Temporada(
                data.getYear() + "-T" + trimestre,
                trimestre + "º trimestre de " + data.getYear(),
                inicio,
                fim
        );
    }

    public static Temporada de(Instant instante) {
        return de(LocalDate.ofInstant(instante, ZONA));
    }

    public static Temporada atual() {
        return de(LocalDate.now(ZONA));
    }

    public static Temporada parse(String id) {
        String[] partes = id.split("-T");
        int ano = Integer.parseInt(partes[0]);
        int trimestre = Integer.parseInt(partes[1]);
        if (trimestre < 1 || trimestre > 4) {
            throw new IllegalArgumentException("Trimestre inválido");
        }
        return de(LocalDate.of(ano, (trimestre - 1) * 3 + 1, 1));
    }

    public long diasRestantes(LocalDate hoje) {
        return Math.max(0, ChronoUnit.DAYS.between(hoje, fim));
    }

    /** Segunda-feira da semana ISO da data, usada como id de semana das missões. */
    public static LocalDate inicioSemana(LocalDate data) {
        return data.minusDays(data.getDayOfWeek().getValue() - 1L);
    }
}
