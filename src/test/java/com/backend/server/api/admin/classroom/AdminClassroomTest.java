package com.backend.server.api.admin.classroom;

import static com.backend.server.fixture.ClassroomFixture.강의실1;
import static com.backend.server.fixture.ClassroomFixture.강의실2;
import static com.backend.server.fixture.UserFixture.관리자1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.server.api.admin.classroom.dto.AdminClassroomResponse;
import com.backend.server.api.admin.classroom.dto.AdminClassroomSearchRequest;
import com.backend.server.api.admin.classroom.dto.AdminClassroomSearchRequest.SearchType;
import com.backend.server.api.admin.classroom.dto.AdminClassroomSearchRequest.SortBy;
import com.backend.server.api.common.dto.ApiResponse;
import com.backend.server.model.entity.classroom.Classroom;
import com.backend.server.model.repository.classroom.ClassroomRepository;
import com.backend.server.model.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("강의실 관리")
public class AdminClassroomTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ClassroomRepository classroomRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @Nested
    class 강의실_목록_조회_API는 {
        @Test
        public void 강의실명으로_검색이_가능하다() throws Exception {
            //given
            userRepository.save(관리자1.엔티티_생성(passwordEncoder, null));
            Classroom classroom = classroomRepository.save(강의실1.엔티티_생성(null));

            AdminClassroomSearchRequest request = new AdminClassroomSearchRequest();
            request.setSearchKeyword(강의실1.getName());
            request.setSearchType(SearchType.NAME);

            //when
            ResultActions result = mockMvc.perform(
                    get("/api/admin/classroom", objectMapper.writeValueAsString(request)));

            //then
            AdminClassroomResponse classroomResponse = new AdminClassroomResponse(classroom, null);
            ApiResponse<?> apiResponse = ApiResponse.success("강의실 목록 조회 성공", List.of(classroomResponse));
            String response = objectMapper.writeValueAsString(apiResponse);

            result.andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(response));
        }

        @Test
        public void ID순_정렬이_가능하다() throws Exception {
            //given
            userRepository.save(관리자1.엔티티_생성(passwordEncoder, null));
            Classroom classroom2 = classroomRepository.save(강의실2.엔티티_생성(null));
            Classroom classroom1 = classroomRepository.save(강의실1.엔티티_생성(null));

            AdminClassroomSearchRequest request = new AdminClassroomSearchRequest();
            request.setSortBy(SortBy.ID);

            //when
            ResultActions result = mockMvc.perform(
                    get("/api/admin/classroom", objectMapper.writeValueAsString(request)));

            //then
            AdminClassroomResponse classroomResponse2 = new AdminClassroomResponse(classroom2, null);
            AdminClassroomResponse classroomResponse1 = new AdminClassroomResponse(classroom1, null);
            ApiResponse<?> apiResponse = ApiResponse.success("강의실 목록 조회 성공",
                    List.of(classroomResponse2,  classroomResponse1));
            String response = objectMapper.writeValueAsString(apiResponse);

            result.andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(response));
        }
    }
}
