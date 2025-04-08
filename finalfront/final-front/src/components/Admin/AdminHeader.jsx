const AdminHeader = () => {
    return (
      <div className="bg-gray-900 text-white p-4 flex justify-between">
        <h1 className="text-lg font-bold">관리자 대시보드</h1>
        <button className="bg-red-500 px-3 py-1 rounded">로그아웃</button>
      </div>
    );
  };
  
  export default AdminHeader;
  