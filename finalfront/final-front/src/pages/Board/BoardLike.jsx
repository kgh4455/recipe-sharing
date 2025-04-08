import { useState, useEffect } from 'react';
import axios from 'axios';
import { FaThumbsUp, FaThumbsDown } from 'react-icons/fa';
import './BoardLike.css';

const BoardLike = ({ boardId }) => {
  const [likeStatus, setLikeStatus] = useState('none'); // 'like', 'dislike', 'none'
  const [likeCount, setLikeCount] = useState(0);
  const [dislikeCount, setDislikeCount] = useState(0);

  const token = localStorage.getItem('token'); // 로컬 스토리지에서 토큰 가져오기
  let userEmail = null;

  // 토큰에서 이메일 추출
  if (token) {
    try {
      const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
      const decodedToken = JSON.parse(atob(base64)); // 디코딩
      userEmail = decodedToken.sub; // 이메일은 'sub' 필드에서 가져오기
    } catch (error) {
      console.error("토큰 디코딩 실패:", error);
    }
  }

  // 페이지 로딩 시, 좋아요/싫어요 상태 및 카운트 가져오기
  useEffect(() => {
    if (!boardId) {
      console.error('Board ID is missing or undefined');
      return;
    }

    axios
      .get(`http://localhost:8080/api/board/${boardId}/likestatus`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        const { likeStatus, likeCount, dislikeCount } = response.data;
        setLikeStatus(likeStatus);
        setLikeCount(likeCount);
        setDislikeCount(dislikeCount);
      })
      .catch((error) => console.error('Error fetching like status:', error));
  }, [boardId, token]);

  // 좋아요 상태 업데이트
  const handleLike = async () => {
    if (!userEmail) {
      alert('로그인이 필요합니다.');
      return;
    }

    if (likeStatus === 'like') {
      // 좋아요 상태 취소
      try {
        await axios.post(
          `http://localhost:8080/api/board/${boardId}/removeLike`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setLikeStatus('none');
        setLikeCount((prevCount) => prevCount - 1);
      } catch (error) {
        console.error('Error removing like status:', error);
      }
      return;
    }

    if (likeStatus === 'dislike') {
      // 싫어요 상태에서 좋아요로 변경
      try {
        await axios.post(
          `http://localhost:8080/api/board/${boardId}/like`,
          { likeType: 'like', userEmail },
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setLikeStatus('like');
        setLikeCount((prevCount) => prevCount + 1);
        setDislikeCount((prevCount) => prevCount - 1);
      } catch (error) {
        console.error('Error updating like status:', error);
      }
      return;
    }

    // 좋아요 추가
    try {
      await axios.post(
        `http://localhost:8080/api/board/${boardId}/like`,
        { likeType: 'like', userEmail },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setLikeStatus('like');
      setLikeCount((prevCount) => prevCount + 1);
    } catch (error) {
      console.error('Error updating like status:', error);
    }
  };

  // 싫어요 상태 업데이트
  const handleDislike = async () => {
    if (!userEmail) {
      alert('로그인이 필요합니다.');
      return;
    }

    if (likeStatus === 'dislike') {
      // 싫어요 상태 취소
      try {
        await axios.post(
          `http://localhost:8080/api/board/${boardId}/removeLike`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setLikeStatus('none');
        setDislikeCount((prevCount) => prevCount - 1);
      } catch (error) {
        console.error('Error removing dislike status:', error);
      }
      return;
    }

    if (likeStatus === 'like') {
      // 좋아요 상태에서 싫어요로 변경
      try {
        await axios.post(
          `http://localhost:8080/api/board/${boardId}/like`,
          { likeType: 'dislike', userEmail },
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setLikeStatus('dislike');
        setDislikeCount((prevCount) => prevCount + 1);
        setLikeCount((prevCount) => prevCount - 1);
      } catch (error) {
        console.error('Error updating dislike status:', error);
      }
      return;
    }

    // 싫어요 추가
    try {
      await axios.post(
        `http://localhost:8080/api/board/${boardId}/like`,
        { likeType: 'dislike', userEmail },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setLikeStatus('dislike');
      setDislikeCount((prevCount) => prevCount + 1);
    } catch (error) {
      console.error('Error updating dislike status:', error);
    }
  };

  return (
    <div className="likedislike-content">
      <button onClick={handleLike} disabled={likeStatus === 'dislike'} style={{ display: 'flex', alignItems: 'center' }}>
        <FaThumbsUp color={likeStatus === 'like' ? '#FFA575' : 'gray'} style={{ fontSize: '30px' }} />
        <div style={{ marginLeft: '5px', fontSize: '24px', lineHeight: '30px' }}>{likeCount}</div>
      </button>
      <button onClick={handleDislike} disabled={likeStatus === 'like'}style={{ display: 'flex', alignItems: 'center' }} > 
        <FaThumbsDown color={likeStatus === 'dislike' ? '#FFA575' : 'gray'}style={{ fontSize: '30px' }}  />
        <div style={{ marginLeft: '5px', fontSize: '24px', lineHeight: '30px' }}>{dislikeCount}</div>
      </button>
    </div>
  );
};

export default BoardLike;
