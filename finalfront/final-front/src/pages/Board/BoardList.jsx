import { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import './BoardList.css';

const BoardList = () => {
  const [boardList, setBoardList] = useState([]);  // 게시글 목록
  const [page, setPage] = useState(1);   // 현재 페이지
  const [size, setSize] = useState(10);   // 한 페이지당 개수
  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [findStr, setFindStr] = useState(''); // 검색어
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsLoggedIn(!!token); // token이 있으면 로그인 상태로 설정
  }, []);

  // 게시글 목록 가져오기
  const fetchBoardList = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/board', {
        params: {
          page: page,
          size: size,
          findStr: findStr,
        },
      });

      if (response.data && response.data.boardList && response.data.pageVO) {
        setBoardList(response.data.boardList); 
        setTotalPages(response.data.pageVO.totalPages || 1);
      } else {
        console.error('응답 데이터 구조가 예상과 다릅니다.', response.data);
      }
    } catch (error) {
      console.error('게시글 목록 가져오기 실패:', error);
    }
  };

  useEffect(() => {
    fetchBoardList();
  }, [page, size]);

  const handleSearchChange = (event) => {
    setFindStr(event.target.value);
  };

  const handleClick=(boardId)=>{
    try {
      // 서버에 조회수 증가 요청
      axios.get(`http://localhost:8080/api/board/${boardId}/incrementviews`);
    } catch (error) {
      console.error("조회수 증가 실패:", error);
    }
  }
  const handleSearchClick = () => {
    setPage(1);
    fetchBoardList();
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const renderPagination = () => {
    const pages = [];
    for (let i = 1; i <= totalPages; i++) {
      pages.push(
        <button 
          key={i}
          className={i === page ? "active" : ""}
          onClick={() => handlePageChange(i)}
          disabled={i === page}
          style={{ margin: '0 5px' }}
        >
          {i}
        </button>
      );
    }
    return pages;
  };

  return (
    <div className="page">
      <h1 className="big-title">요리 고민방</h1>
      
      <div className="search-container-board">
        <input
          type="text"
          value={findStr}
          onChange={handleSearchChange}
          placeholder="검색어를 입력하세요"
          className="search-board"
        />
        <button onClick={handleSearchClick} className="search-btnn">검색</button>
      </div>
      
      <table>
        <thead className="thz"></thead>
        <tbody className="tdz">
          {boardList.map((board) => (
            <tr key={board.boardId}>
              <td>{board.boardId}</td>
              <td> 
                <Link to={`/boardlist/${board.boardId}`} style={{ textDecoration: "none", color: "inherit" }} onClick={()=>handleClick(board.boardId)}>
                  {board.title}
                </Link>
              </td>
              <td>{board.createdAt}</td>
              <td>{board.views}</td>
              <td>{board.author}</td>
            </tr>
          ))}
        </tbody>
      </table>
      
      {/* 글쓰기 버튼이 로그인 상태일 때만 보이도록 */}
      {isLoggedIn && (
        <div className="write-button-container">
          <Link to={"/boardwrite"}>  
            <button className="write-btn">글쓰기</button>
          </Link>
        </div>
      )}
      
      <div className="board-paging">
        {renderPagination()}
      </div>
    </div>
  );
};

export default BoardList;
