package com.example.kloset_lab.global.ai.client;

import com.example.kloset_lab.global.ai.dto.*;
import java.util.List;
import org.springframework.context.annotation.Profile;

@Profile("prod")
public class HttpAIClient implements AIClient {
    @Override
    public ValidateResponse validateImages(Long userId, List<String> imageUrlList) {
        return null;
    }

    @Override
    public BatchResponse analyzeImages(Long userId, List<String> imageUrlList) {
        return null;
    }

    @Override
    public BatchResponse getBatchStatus(String batchId) {
        return null;
    }

    @Override
    public EmbeddingResponse saveEmbedding(EmbeddingRequest request) {
        return null;
    }

    @Override
    public void deleteClothes(Long clothesId) {}

    @Override
    public OutfitResponse recommendOutfit(Long userId, String query) {
        return null;
    }

    @Override
    public ShopResponse searchShop(Long userId, String query) {
        return null;
    }
}
