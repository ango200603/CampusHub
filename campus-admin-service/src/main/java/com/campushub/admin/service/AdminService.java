package com.campushub.admin.service;

import com.campushub.admin.vo.AdminStatsVO;
import java.util.List;
import java.util.Map;

public interface AdminService {
    List<Map<String, Object>> users();

    List<Map<String, Object>> orders();

    List<Map<String, Object>> aiTasks();

    List<Map<String, Object>> files();

    AdminStatsVO stats();
}
