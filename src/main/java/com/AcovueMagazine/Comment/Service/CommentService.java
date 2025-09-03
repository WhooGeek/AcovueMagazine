package com.AcovueMagazine.Comment.Service;
import com.AcovueMagazine.Comment.DTO.CommentReqDTO;
import com.AcovueMagazine.Comment.DTO.CommentResDTO;
import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import com.AcovueMagazine.User.Entity.UserRoll;
import com.AcovueMagazine.User.Entity.Users;
import com.AcovueMagazine.User.Repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MagazineRepository magazineRepository;
    private final UsersRepository usersRepository;

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

    // 댓글 등록
    @Transactional
    public CommentResDTO createComment(Long magazineId, CommentReqDTO commentReqDTO) {

        Users users = usersRepository.findById(commentReqDTO.getUserSeq())
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 매거진 유효성 검사
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        Comment parentComment = null;

        if(commentReqDTO.getParentSeq() != null){
            parentComment = commentRepository.findById(commentReqDTO.getParentSeq())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다." + commentReqDTO.getParentSeq()));
        }

        Comment comment = new Comment(users, magazine, commentReqDTO.getCommentContent(), parentComment);

        commentRepository.save(comment);

        return CommentResDTO.fromEntity(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResDTO updateComment(Long magazineId, Long commentSeq, CommentReqDTO commentReqDTO) {

        // 유저 조회
        Users users = usersRepository.findById(commentReqDTO.getUserSeq())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다. ID = " + commentReqDTO.getUserSeq()) );

        // 매거진 조회
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        // 댓글 조회
        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다." + commentReqDTO.getCommentSeq()));

        // 권한 체크
        if (!magazine.getUser().getUserSeq().equals(users.getUserSeq()) &&
                users.getUserRoll() != UserRoll.ADMIN) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 내용 수정이 있으면 저장
        if(commentReqDTO.getCommentContent() != null && !commentReqDTO.getCommentContent().isEmpty()){
            comment.updateContent(commentReqDTO.getCommentContent());
        }

        return CommentResDTO.fromEntity(comment);
    }
}
