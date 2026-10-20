package com.gtnix.inovagab.dto.ai;

import java.util.List;

public record ScoreIdeasResponse(
        List<IdeaScoreItem> ranking,
        String modelo,
        boolean fallback
) {
}
