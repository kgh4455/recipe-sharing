package com.project.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.project.model.TarotCard;

@Mapper
public interface TarotMapper {

    // ✅ 랜덤 타로 카드 4장 가져오기
    @Select("SELECT id, name, 'https://example.com/card-back.jpg' AS backImage FROM tarot_cards ORDER BY RAND() LIMIT #{count}")
    List<Map<String, Object>> getRandomTarotCards(@Param("count") int count);

    // ✅ 오늘 선택한 횟수 확인
    @Select("SELECT COUNT(*) FROM tarot_history WHERE user_email = #{email} AND DATE(drawn_at) = CURDATE()")
    int countTodaySelections(@Param("email") String email);

    // ✅ 타로 카드 선택 저장
    @Insert("INSERT INTO tarot_history (user_email, tarot_card_id) VALUES (#{email}, #{tarotCardId})")
    void insertTarotSelection(@Param("email") String email, @Param("tarotCardId") int tarotCardId);

    // ✅ 선택한 타로 카드 정보 가져오기
    @Select("SELECT id, name, description, image_url, created_at FROM tarot_cards WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"), // ✅ 수동 매핑
        @Result(property = "createdAt", column = "created_at")
    })
    TarotCard getTarotCardById(@Param("id") int id);

}