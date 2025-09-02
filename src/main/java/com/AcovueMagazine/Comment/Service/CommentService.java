package com.AcovueMagazine.Comment.Service;
import com.AcovueMagazine.Comment.DTO.CommentResDTO;
import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Comment.Entity.CommentStatus;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MagazineRepository magazineRepository;

    // 댓글 + 대댓글 조회 기능
    @Transactional
    public List<CommentResDTO> getComment(Long magazineId) {

        // 매거진 유효성 검사
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        // 최초 댓글 조회
        List<Comment> topComments = commentRepository.findTopCommentsByMagazine(magazineId);



        List<CommentResDTO> result = new ArrayList<>();

        for (Comment comment : topComments) {
            CommentResDTO commentDTO = CommentResDTO.fromEntity(comment);

            //  대댓글 조회
            List<Comment> childComments = commentRepository.findByParent(comment.getCommentSeq());

            List<CommentResDTO> childDTOs = new ArrayList<>();
            for (Comment child : childComments) {
                childDTOs.add(CommentResDTO.fromEntity(child));
            }

            // DTO에 대댓글 세팅
            commentDTO.setChildren(childDTOs);
            result.add(commentDTO);
        }
        return result;
    }


}
