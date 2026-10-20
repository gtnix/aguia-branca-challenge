package com.gtnix.inovagab.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "apoios")
@CompoundIndex(name = "idx_apoios_ideia_user_unique", def = "{'ideiaId': 1, 'userId': 1}", unique = true)
public class ApoioDocument {

    @Id
    private String id;
    private String ideiaId;
    private String userId;
    private Instant criadoEm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdeiaId() {
        return ideiaId;
    }

    public void setIdeiaId(String ideiaId) {
        this.ideiaId = ideiaId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
