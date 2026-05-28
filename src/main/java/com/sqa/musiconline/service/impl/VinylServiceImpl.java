package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.RetailerVinylSaveDTO;
import com.sqa.musiconline.dto.VinylSearchRequestDTO;
import com.sqa.musiconline.entity.Vinyl;
import com.sqa.musiconline.mapper.VinylMapper;
import com.sqa.musiconline.service.VinylService;
import com.sqa.musiconline.vo.RetailerVinylManageVO;
import com.sqa.musiconline.vo.VinylCardVO;
import com.sqa.musiconline.vo.VinylDetailVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class VinylServiceImpl implements VinylService {

    private final VinylMapper vinylMapper;

    public VinylServiceImpl(VinylMapper vinylMapper) {
        this.vinylMapper = vinylMapper;
    }

    @Override
    public List<VinylCardVO> searchPublicVinyls(VinylSearchRequestDTO request) {
        LambdaQueryWrapper<Vinyl> query = new LambdaQueryWrapper<Vinyl>()
                .eq(Vinyl::getListingStatus, "ACTIVE")
                .gt(Vinyl::getStockQuantity, 0)
                .orderByDesc(Vinyl::getId);

        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            query.and(wrapper -> wrapper
                    .like(Vinyl::getArtistName, keyword)
                    .or()
                    .like(Vinyl::getTitle, keyword)
                    .or()
                    .like(Vinyl::getGenreName, keyword));
        }
        if (StringUtils.hasText(request.getArtistName())) {
            query.like(Vinyl::getArtistName, request.getArtistName().trim());
        }
        if (StringUtils.hasText(request.getTitle())) {
            query.like(Vinyl::getTitle, request.getTitle().trim());
        }
        if (StringUtils.hasText(request.getFormatType())) {
            query.eq(Vinyl::getFormatType, request.getFormatType().trim().toUpperCase());
        }
        if (StringUtils.hasText(request.getGenreName())) {
            query.like(Vinyl::getGenreName, request.getGenreName().trim());
        }

        return vinylMapper.selectList(query).stream()
                .map(vinyl -> new VinylCardVO(
                        vinyl.getId(),
                        vinyl.getArtistName(),
                        vinyl.getTitle(),
                        vinyl.getFormatType(),
                        vinyl.getGenreName(),
                        vinyl.getConditionGrade(),
                        vinyl.getPrice(),
                        vinyl.getCoverImageUrl(),
                        vinyl.getStockQuantity()
                ))
                .toList();
    }

    @Override
    public VinylDetailVO getPublicVinylDetail(Long vinylId) {
        Vinyl vinyl = vinylMapper.selectOne(new LambdaQueryWrapper<Vinyl>()
                .eq(Vinyl::getId, vinylId)
                .eq(Vinyl::getListingStatus, "ACTIVE")
                .gt(Vinyl::getStockQuantity, 0)
                .last("LIMIT 1"));
        if (vinyl == null) {
            throw new IllegalArgumentException("Vinyl listing not found.");
        }
        return new VinylDetailVO(
                vinyl.getId(),
                vinyl.getSellerUserId(),
                vinyl.getArtistName(),
                vinyl.getTitle(),
                vinyl.getFormatType(),
                vinyl.getGenreName(),
                vinyl.getConditionGrade(),
                vinyl.getReleaseDate(),
                vinyl.getPrice(),
                vinyl.getStockQuantity(),
                vinyl.getDescription(),
                vinyl.getCoverImageUrl()
        );
    }

    @Override
    public List<RetailerVinylManageVO> getRetailerVinyls(RequestUserContext.CurrentUser currentUser) {
        ensureRetailer(currentUser);
        return vinylMapper.selectList(new LambdaQueryWrapper<Vinyl>()
                        .eq(Vinyl::getSellerUserId, currentUser.user().getId())
                        .orderByDesc(Vinyl::getId))
                .stream()
                .map(vinyl -> new RetailerVinylManageVO(
                        vinyl.getId(),
                        vinyl.getArtistName(),
                        vinyl.getTitle(),
                        vinyl.getFormatType(),
                        vinyl.getGenreName(),
                        vinyl.getConditionGrade(),
                        vinyl.getReleaseDate(),
                        vinyl.getPrice(),
                        vinyl.getStockQuantity(),
                        vinyl.getDescription(),
                        vinyl.getCoverImageUrl(),
                        vinyl.getListingStatus()
                ))
                .toList();
    }

    @Override
    public void createRetailerVinyl(RequestUserContext.CurrentUser currentUser, RetailerVinylSaveDTO request) {
        ensureRetailer(currentUser);
        Vinyl vinyl = new Vinyl();
        populateRetailerVinyl(vinyl, currentUser.user().getId(), request);
        vinyl.setListingStatus(resolveListingStatus(request.getStockQuantity()));
        vinylMapper.insert(vinyl);
    }

    @Override
    public void updateRetailerVinyl(RequestUserContext.CurrentUser currentUser, Long vinylId, RetailerVinylSaveDTO request) {
        ensureRetailer(currentUser);
        Vinyl vinyl = getOwnedVinyl(currentUser.user().getId(), vinylId);
        populateRetailerVinyl(vinyl, currentUser.user().getId(), request);
        vinyl.setListingStatus(resolveListingStatus(request.getStockQuantity()));
        vinylMapper.updateById(vinyl);
    }

    @Override
    public void deleteRetailerVinyl(RequestUserContext.CurrentUser currentUser, Long vinylId) {
        ensureRetailer(currentUser);
        Vinyl vinyl = getOwnedVinyl(currentUser.user().getId(), vinylId);
        vinyl.setListingStatus("REMOVED");
        vinylMapper.updateById(vinyl);
    }

    private void ensureRetailer(RequestUserContext.CurrentUser currentUser) {
        if (!currentUser.hasRole("ROLE_RETAILER")) {
            throw new IllegalArgumentException("Retailer role is required.");
        }
    }

    private Vinyl getOwnedVinyl(Long sellerUserId, Long vinylId) {
        Vinyl vinyl = vinylMapper.selectOne(new LambdaQueryWrapper<Vinyl>()
                .eq(Vinyl::getId, vinylId)
                .eq(Vinyl::getSellerUserId, sellerUserId)
                .last("LIMIT 1"));
        if (vinyl == null) {
            throw new IllegalArgumentException("Vinyl listing not found for the current retailer.");
        }
        return vinyl;
    }

    private void populateRetailerVinyl(Vinyl vinyl, Long sellerUserId, RetailerVinylSaveDTO request) {
        String formatType = request.getFormatType().trim().toUpperCase();
        if (!List.of("ALBUM", "SINGLE", "EP").contains(formatType)) {
            throw new IllegalArgumentException("Format type must be ALBUM, SINGLE, or EP.");
        }

        vinyl.setSellerUserId(sellerUserId);
        vinyl.setArtistName(request.getArtistName().trim());
        vinyl.setTitle(request.getTitle().trim());
        vinyl.setFormatType(formatType);
        vinyl.setGenreName(trimToNull(request.getGenreName()));
        vinyl.setConditionGrade(request.getConditionGrade().trim());
        vinyl.setReleaseDate(request.getReleaseDate());
        vinyl.setPrice(request.getPrice());
        vinyl.setStockQuantity(request.getStockQuantity());
        vinyl.setDescription(trimToNull(request.getDescription()));
        vinyl.setCoverImageUrl(trimToNull(request.getCoverImageUrl()));
    }

    private String resolveListingStatus(Integer stockQuantity) {
        return stockQuantity != null && stockQuantity > 0 ? "ACTIVE" : "OUT_OF_STOCK";
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
