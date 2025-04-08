import { useState } from 'react';
import axios from 'axios';
import './ClubApply.css';
import { useNavigate, useParams } from 'react-router-dom';

const ClubApply = () => {
  const [name, setName] = useState('');
  const [age, setAge] = useState('');
  const [gender, setGender] = useState('');
  const [email, setEmail] = useState('');
  const [agree, setAgree] = useState(false); // 개인정보 동의 여부
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();
  const { clubId } = useParams(); // URL에서 clubId를 가져옵니다.

  // 폼 제출 처리 함수
  const handleSubmit = async (e) => {
    e.preventDefault();

    // 기본 유효성 검사
    if (!name || !age || !gender || !email || !agree) {
      setError('모든 항목을 올바르게 작성해 주세요.');
      return;
    }

    // 나이 입력이 숫자 형식인지 체크
    if (isNaN(age)) {
      setError('나이는 숫자로 입력해 주세요.');
      return;
    }

    const applicantData = {
      clubId: clubId,
      applicantName: name, // name은 사용자가 입력한 이름
      applicantEmail: email, // email은 사용자가 입력한 이메일
      applicantAge: parseInt(age), // 나이는 숫자로 변환하여 보내기
      applicantGender: gender, // gender는 사용자가 선택한 성별
      privacyAgreement: agree, // 개인정보 동의 여부
    };

    try {
      const response = await axios.post(
        `http://localhost:8080/api/club/${clubId}/send-application`, // clubId를 동적으로 삽입
        applicantData
      );
      setSuccess('모임 신청이 완료되었습니다!');
      setError(''); // 이전 오류 메시지 제거
      // 폼 초기화
      setName('');
      setAge('');
      setGender('');
      setEmail('');
      setAgree(false);
      alert('신청이 완료되었습니다!');
      navigate('/clublist'); // 신청 후 클럽 리스트로 이동
    } catch (err) {
      console.error(err);
      setError('신청을 처리하는 중 오류가 발생했습니다.');
      setSuccess(''); // 성공 메시지 제거
    }
  };

  return (
    <div className="club-apply-page">
      <h1 className="big-title">모임 신청</h1>
      
      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}

      <div className="gyeonggo">
        <p><strong>⚠️ 모임 신청은 신중하게 결정해주세요! ⚠️</strong></p><br/>
        <p>모임에 신청하기 전에 아래 내용을 꼭 확인해 주세요</p><br/>
        1. 모임 신청 후에는 취소나 변경이 어려울 수 있습니다. 신청을 완료하기 전에 신청 대상, 모임의 일정 및 장소 등을 다시 한 번 확인해 주세요. <br/>
        2. 모임의 목적이나 진행 방식이 여러분의 기대와 일치하는지 확인하십시오. 모임은 많은 사람들과 함께하는 활동이므로, 미리 잘 고려해보는 것이 중요합니다.<br/>
        3. 개인정보 동의란을 필수로 체크해야만 신청이 완료됩니다. 개인정보를 제공하기 전에 반드시 동의 여부를 확인하세요.<br/><br/>
        <p>신중하게 고려하고 신청해주세요. 여러분의 참여가 모임에 큰 도움이 됩니다!</p>
      </div>

      <form onSubmit={handleSubmit} className="apply-form">
        <div className="form-group">
          <label htmlFor="name">이름을 입력하세요</label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="EX: 홍길동"
            required
            className="input-field"
          />
        </div>

        <div className="form-group">
          <label htmlFor="age">나이를 입력하세요</label>
          <input
            type="number"
            id="age"
            value={age}
            onChange={(e) => setAge(e.target.value)}
            placeholder="EX: 25"
            className="input-field"
            required
          />
        </div>

        <div className="form-group">
          <label>성별</label>
          <div className="gender-options">
            <label>
              <input
                type="radio"
                name="gender"
                value="남성"
                checked={gender === '남성'}
                onChange={(e) => setGender(e.target.value)}
              />
              남성
            </label>
            <label>
              <input
                type="radio"
                name="gender"
                value="여성"
                checked={gender === '여성'}
                onChange={(e) => setGender(e.target.value)}
              />
              여성
            </label>
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="email">신청 결과를 받을 이메일을 입력하세요</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="EX: abcd@gmail.com"
            className="input-field"
            required
          />
        </div>

        <div className="form-group">
          <label>
            <input
              type="checkbox"
              checked={agree}
              onChange={(e) => setAgree(e.target.checked)}
              required
            />
            개인정보 수집 및 이용에 동의합니다.
          </label>
        </div>

        <div className="button-container">
        <button className="back-button" onClick={() => navigate('/clublist')}>
              돌아가기
            </button>
            <button type="submit" className="applicant-button">신청하기</button>
        </div>
      </form>
    </div>
  );
};

export default ClubApply;
