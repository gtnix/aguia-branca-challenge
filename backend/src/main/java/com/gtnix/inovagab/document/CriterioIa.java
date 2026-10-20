package com.gtnix.inovagab.document;

/** Nota de 1 a 5 da IA para um critério da rubrica, com o trecho da ideia que a justifica. */
public class CriterioIa {

    private String criterio;
    private int nota;
    private String evidencia;

    public CriterioIa() {
    }

    public CriterioIa(String criterio, int nota, String evidencia) {
        this.criterio = criterio;
        this.nota = nota;
        this.evidencia = evidencia;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }
}
