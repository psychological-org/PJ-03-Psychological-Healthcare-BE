package com.microservices.community.participant_community;

import com.microservices.community.community.CommunityService;
import com.microservices.community.exception.ParticipantCommunityNotFoundException;
import com.microservices.community.exception.UserNotFoundException;
import com.microservices.community.user.UserClient;
import com.microservices.community.user.UserResponse;
import com.microservices.community.utils.PagedResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantCommunityService {
    private final ParticipantCommunityRepository participantCommunityRepository;
    private final ParticipantCommunityMapper participantCommunityMapper;
    private final CommunityService communityService;
    private final UserClient userClient;

    public Integer createParticipantCommunity(ParticipantCommunityRequest request) {
        try {
            ResponseEntity<UserResponse> response = userClient.findById(request.userId());
            if (response == null || response.getBody() == null) {
                throw new UserNotFoundException(
                        String.format("Cannot create community_seen:: No user found with ID: %s", request.userId()));
            }
            var collectionResponse = communityService.findById(request.communityId());
            if (collectionResponse == null ) {
                throw new UserNotFoundException(
                        String.format("Cannot create community_seen:: No community found with ID: %s", request.communityId()));
            }

            ParticipantCommunity participantCommunity = participantCommunityMapper.toParticipantCommunity(request);
            return participantCommunityRepository.save(participantCommunity).getId();

        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User or community service not available or returned error: " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Unexpected error when creating participant community", e);
        }
    }

    public void updateParticipantCommunity(ParticipantCommunityRequest request) {
        var participantCommunity = this.participantCommunityRepository.findById(request.id())
                .orElseThrow(() -> new ParticipantCommunityNotFoundException(
                        String.format("Cannot update participant community:: No participant community found with the provided ID: %s",
                                request.id())));
        mergeParticipantCommunity(participantCommunity, request);
        this.participantCommunityRepository.save(participantCommunity);
    }

    void mergeParticipantCommunity(ParticipantCommunity participantCommunity, ParticipantCommunityRequest request) {
        if (request.userId() != null) {
            try {
                ResponseEntity<UserResponse> response = userClient.findById(request.userId());
                if (response == null || response.getBody() == null) {
                    throw new UserNotFoundException(
                            String.format("Cannot update collection_seen:: No user found with ID: %s", request.userId()));
                }
            } catch (FeignException.FeignClientException e) {
                throw new UserNotFoundException("User service not available or returned error: " + e.getMessage());
            }
            participantCommunity.setUserId(request.userId());
        }
        if (request.communityId() != null) {
            // var collectionResponse = communityService.findById(request.communityId());
            participantCommunity.setCommunityId(request.communityId());
        }
    }

    public ParticipantCommunityResponse findById(Integer id) {
        return this.participantCommunityRepository.findOneById(id)
                .map(participantCommunity -> participantCommunityMapper.fromParticipantCommunityResponse(participantCommunity, userClient))
                .orElseThrow(() -> new ParticipantCommunityNotFoundException(
                        String.format("No participant community found with the provided ID: %s", id)));
    }

    public void deleteById(Integer id) {
        this.participantCommunityRepository.findById(id)
                .orElseThrow(() -> new ParticipantCommunityNotFoundException(
                        String.format("Cannot delete participant community:: No participant community found with the provided ID: %s",
                                id)));
        this.participantCommunityRepository.softDeleteById(id);
    }

    public boolean existsById(Integer id) {
        ParticipantCommunityResponse participantCommunity = this.findById(id);
        return participantCommunity != null;
    }

    public PagedResponse<ParticipantCommunityResponse> findAllParticipantCommunity(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ParticipantCommunity> appointments = this.participantCommunityRepository.findAll(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new ParticipantCommunityNotFoundException("No collection found");
        }
        List<ParticipantCommunityResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(participantCommunity -> participantCommunityMapper.fromParticipantCommunityResponse(participantCommunity, userClient))
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    // get  all participant_community by userId
    public PagedResponse<ParticipantCommunityResponse> findByUserId(String userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ParticipantCommunity> appointments = this.participantCommunityRepository.findByUserId(userId, pageable);
        if (appointments.getContent().isEmpty()) {
            throw new ParticipantCommunityNotFoundException("No collection found");
        }
        List<ParticipantCommunityResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(participantCommunity -> participantCommunityMapper.fromParticipantCommunityResponse(participantCommunity, userClient))
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    // get  all participant_community by communityId
    public PagedResponse<ParticipantCommunityResponse> findByCommunityId(Integer communityId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ParticipantCommunity> appointments = this.participantCommunityRepository.findByCommunityId(communityId, pageable);
        if (appointments.getContent().isEmpty()) {
            throw new ParticipantCommunityNotFoundException("No collection found");
        }
        List<ParticipantCommunityResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(participantCommunity -> participantCommunityMapper.fromParticipantCommunityResponse(participantCommunity, userClient))
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public List<ParticipantCommunityResponse> findByCommunityIdNotPaginate(Integer communityId) {
        List<ParticipantCommunity> appointments = this.participantCommunityRepository.findByCommunityIdNotPaginate(communityId);
        if (appointments.isEmpty()) {
            throw new ParticipantCommunityNotFoundException("No collection found");
        }
        // Sử dụng lambda để truyền userClient vào phương thức từ mapper
        return appointments.stream()
                .map(participantCommunity -> this.participantCommunityMapper.fromParticipantCommunityResponse(participantCommunity, userClient))
                .collect(Collectors.toList());
    }


}
