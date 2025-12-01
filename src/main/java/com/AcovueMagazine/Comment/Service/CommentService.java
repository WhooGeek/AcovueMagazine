package com.AcovueMagazine.Comment.Service;
import com.AcovueMagazine.Comment.Dto.CommentReqDTO;
import com.AcovueMagazine.Comment.Dto.CommentResDTO;
import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
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
    private final MembersRepository membersRepository;

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

    // 댓글 + 대댓글 등록
    @Transactional
    public CommentResDTO createComment(Long magazineId, CommentReqDTO commentReqDTO) {

        Members members = membersRepository.findById(commentReqDTO.getUserSeq())
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 매거진 유효성 검사
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        Comment parentComment = null;

        if(commentReqDTO.getParentSeq() != null){
            parentComment = commentRepository.findById(commentReqDTO.getParentSeq())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다." + commentReqDTO.getParentSeq()));
        }

        Comment comment = new Comment(members, magazine, commentReqDTO.getCommentContent(), parentComment);

        commentRepository.save(comment);

        return CommentResDTO.fromEntity(comment);
    }

    // 댓글 + 대댓글 수정
    @Transactional
    public CommentResDTO updateComment(Long magazineId, Long commentSeq, CommentReqDTO commentReqDTO) {

        // 유저 조회
        Members members = membersRepository.findById(commentReqDTO.getUserSeq())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다. ID = " + commentReqDTO.getUserSeq()) );

        // 매거진 조회
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        // 댓글 조회
        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다." + commentReqDTO.getCommentSeq()));

        // 권한 체크
        if (!magazine.getMembers().getMember_seq().equals(members.getMember_seq()) &&
                members.getMemberRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 내용 수정이 있으면 저장
        if(commentReqDTO.getCommentContent() != null && !commentReqDTO.getCommentContent().isEmpty()){
            comment.updateContent(commentReqDTO.getCommentContent());
        }

        return CommentResDTO.fromEntity(comment);
    }

    // 댓글 + 대댓글 삭제
    @Transactional
    public CommentResDTO deleteComment(Long magazineId, Long commentSeq, CommentReqDTO commentReqDTO) {

        // 유저 조회
        Members members = membersRepository.findById(commentReqDTO.getUserSeq())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다. ID = " + commentReqDTO.getUserSeq()) );

        // 매거진 조회
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        // 댓글 조회
        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다." + commentReqDTO.getCommentSeq()));

        if(comment.getMember().getMemberRole() != MemberRole.ADMIN ||
            members.getMember_seq() == comment.getMember().getMember_seq()) {
            commentRepository.delete(comment);
        } else{
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        return CommentResDTO.fromEntity(comment);

    }
}
