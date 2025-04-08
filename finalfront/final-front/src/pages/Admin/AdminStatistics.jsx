import { useEffect, useState } from "react";
import axios from "axios";
import { Chart } from "react-google-charts";
import "../../styles/AdminStatistics.css";

const AdminStatistics = () => {
  const [signupsData, setSignupsData] = useState([]);
  const [topRecipes, setTopRecipes] = useState([]);
  const [activeUsers, setActiveUsers] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [currentUsers, setCurrentUsers] = useState(0);
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchSignups();
    fetchTopRecipes();
    fetchActiveUsers();
    fetchCurrentUsers();
    fetchCategoryData();
  }, []);

  const fetchSignups = async () => {
    try {
      const res = await axios.get("http://localhost:8080/admin/daily-signups", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const chartData = [["날짜", "가입자 수"]];
      res.data.forEach((row) => {
        chartData.push([row.date, Number(row.count)]);
      });
      setSignupsData(chartData);
    } catch (err) {
      console.error("❌ 회원가입 통계 에러:", err);
    }
  };

  const fetchTopRecipes = async () => {
    try {
      const res = await axios.get("http://localhost:8080/admin/top-viewed-recipes", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const chartData = [["레시피명", "조회수", { role: "style" }, { role: "tooltip" }]];
      res.data.forEach((recipe) => {
        chartData.push([
          recipe.foodname,
          Number(recipe.view),
          "color: #3498db",
          `${recipe.foodname}\n조회수: ${recipe.view}회`,
        ]);
      });
      setTopRecipes(chartData);
    } catch (err) {
      console.error("❌ 인기 레시피 에러:", err);
    }
  };

  const fetchActiveUsers = async () => {
    try {
      const res = await axios.get("http://localhost:8080/admin/most-active-users", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const chartData = [["이메일", "로그인 수", { role: "style" }, { role: "tooltip" }]];
      res.data.forEach((user) => {
        chartData.push([
          user.email,
          Number(user.login_count),
          "color: #e67e22",
          `${user.email}\n로그인 수: ${user.login_count}회`,
        ]);
      });
      setActiveUsers(chartData);
    } catch (err) {
      console.error("❌ 로그인 랭킹 에러:", err);
    }
  };

  const fetchCurrentUsers = async () => {
    try {
      const res = await axios.get("http://localhost:8080/admin/current-users", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCurrentUsers(res.data.active_users);
    } catch (err) {
      console.error("❌ 현재 접속자 수 에러:", err);
    }
  };

  const fetchCategoryData = async () => {
    try {
      const res = await axios.get("http://localhost:8080/admin/recipe-category-count", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const chartData = [["카테고리", "레시피 수"]];
      res.data.forEach((item) => {
        chartData.push([item.category_name, Number(item.count)]);
      });
      setCategoryData(chartData);
    } catch (err) {
      console.error("❌ 카테고리별 레시피 수 에러:", err);
    }
  };

  return (
    <div className="adminz-container">
        <h1 className="big-title">📊 페이지 통계</h1>
        <h3>🟢 현재 접속자 수: {currentUsers} 명</h3>
      <div className="statistics-grid">
        <div className="chart-box">
          <h3>📅 일별 회원가입 수</h3>
          {signupsData.length > 1 && (
            <Chart
              chartType="LineChart"
              width="100%"
              height="300px"
              data={signupsData}
              options={{
                curveType: "function",
                legend: { position: "bottom" },
                hAxis: { title: "날짜" },
                vAxis: { title: "가입자 수" },
              }}
            />
          )}
        </div>
        <div className="chart-box">
          <h3>🔥 인기 레시피 TOP 5</h3>
          {topRecipes.length > 1 && (
            <Chart
              chartType="ColumnChart"
              width="100%"
              height="300px"
              data={topRecipes}
              options={{
                legend: "none",
                hAxis: { title: "레시피명" },
                vAxis: { title: "조회수" },
                tooltip: { isHtml: true },
              }}
            />
          )}
        </div>

        <div className="chart-box">
          <h3>🏆 최근 30일 로그인 랭킹 (Top 5)</h3>
          {activeUsers.length > 1 && (
            <Chart
              chartType="BarChart"
              width="100%"
              height="300px"
              data={activeUsers}
              options={{
                legend: "none",
                hAxis: {
                  title: "로그인 수",
                  minValue: 0,
                },
                vAxis: {
                  title: "이메일",
                  textStyle: { fontSize: 12 },
                },
                tooltip: { isHtml: true },
                bar: { groupWidth: "60%" },
              }}
            />
          )}
        </div>

        <div className="chart-box">
          <h3>🍽️ 카테고리별 레시피 수</h3>
          {categoryData.length > 1 && (
            <Chart
              chartType="PieChart"
              width="100%"
              height="300px"
              data={categoryData}
              options={{
                pieHole: 0.3,
                is3D: false,
                legend: { position: "right" },
                chartArea: { width: "90%", height: "80%" },
              }}
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminStatistics;