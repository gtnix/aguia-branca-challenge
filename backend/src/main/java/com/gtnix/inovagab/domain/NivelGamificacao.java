package com.gtnix.inovagab.domain;

public enum NivelGamificacao {
    INICIANTE("Iniciante", 0),
    EM_ASCENSAO("Em Ascensão", 50),
    ENGAJADO("Engajado", 150),
    VISIONARIO("Visionário", 400),
    TRANSFORMADOR("Transformador", 900);

    private final String label;
    private final int xpMinimo;

    NivelGamificacao(String label, int xpMinimo) {
        this.label = label;
        this.xpMinimo = xpMinimo;
    }

    public String getLabel() {
        return label;
    }

    public int getXpMinimo() {
        return xpMinimo;
    }

    public static NivelGamificacao fromXp(int xp) {
        NivelGamificacao atual = INICIANTE;
        for (NivelGamificacao nivel : values()) {
            if (xp >= nivel.xpMinimo) {
                atual = nivel;
            }
        }
        return atual;
    }

    public NivelGamificacao proximo() {
        int idx = ordinal() + 1;
        return idx < values().length ? values()[idx] : null;
    }

    public boolean atinge(NivelGamificacao minimo) {
        return minimo == null || ordinal() >= minimo.ordinal();
    }
}
