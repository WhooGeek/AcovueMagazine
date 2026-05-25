package com.AcovueMagazine.Comment.Service;
import com.AcovueMagazine.Comment.Dto.CommentCountResDTO;
import com.AcovueMagazine.Comment.Dto.CommentReqDTO;
import com.AcovueMagazine.Comment.Dto.CommentResDTO;
import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Comment.Entity.CommentStatus;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.RestApiException;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Post.Repository.PostRepository;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 댓글 + 대댓글 조회 기능
    @Transactional
    public List<CommentResDTO> getComment(Long postId) {

        // 매거진 유효성 검사
        Post magazine = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        // 최초 댓글 조회
        List<Comment> topComments = commentRepository.findTopCommentsByPost(postId);

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
    public CommentResDTO createComment(Long postId, CommentReqDTO commentReqDTO) {

        Members members = getCurrentMember();

        // 매거진 유효성 검사
        Post magazine = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        Comment parentComment = null;

        if(commentReqDTO.getParentSeq() != null){
            parentComment = commentRepository.findById(commentReqDTO.getParentSeq())
                    .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
        }

        Comment comment = new Comment(members, magazine, commentReqDTO.getCommentContent(), parentComment);

        commentRepository.save(comment);

        return CommentResDTO.fromEntity(comment);
    }

    // 댓글 + 대댓글 수정
    @Transactional
    public CommentResDTO updateComment(Long postId, Long commentSeq, CommentReqDTO commentReqDTO) {

        Members members = getCurrentMember();

        // 매거진 조회
        Post magazine = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        // 댓글 조회
        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        boolean isWriter = comment.getMember().getMemberSeq().equals(members.getMemberSeq());
        boolean isAdmin = members.getMemberRole() == MemberRole.ADMIN;

        if (!isWriter && !isAdmin) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        // 내용 수정이 있으면 저장
        if(commentReqDTO.getCommentContent() != null && !commentReqDTO.getCommentContent().isEmpty()){
            comment.updateContent(commentReqDTO.getCommentContent());
        }

        return CommentResDTO.fromEntity(comment);
    }

    // 댓글 + 대댓글 삭제
    @Transactional
    public CommentResDTO deleteComment(Long postId, Long commentSeq) {

        Members members = getCurrentMember();

        // 매거진 조회
        Post magazine = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        // 댓글 조회
        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        boolean isWriter = comment.getMember().getMemberSeq().equals(members.getMemberSeq());
        boolean isAdmin = members.getMemberRole() == MemberRole.ADMIN;

        if (!isWriter && !isAdmin) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        commentRepository.delete(comment);

        return CommentResDTO.fromEntity(comment);

    }

    // 상세 페이지 Comment Count 조회
    @Transactional
    public CommentCountResDTO getCommentCount(Long postId) {

        // 포스트 조회
        postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        Long commentCount = commentRepository.countByPost_PostSeqAndCommentStatus(postId, CommentStatus.ACTIVE);

        return CommentCountResDTO.from(postId, commentCount);
    }

    private Members getCurrentMember() {
        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RestApiException(ErrorCode.ACCESS_TOKEN_NULL);
        }

        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            throw new RestApiException(ErrorCode.INVALID_TOKEN);
        }

        return membersRepository.findById(memberSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
    }
}
