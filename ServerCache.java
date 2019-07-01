package com.actolap.wse.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.actolap.wse.dao.DepartmentDao;
import com.actolap.wse.dao.UserDao;
import com.actolap.wse.dto.UserDepartment;
import com.actolap.wse.enums.DepartmentStatus;
import com.actolap.wse.enums.UserStatus;
import com.actolap.wse.model.Department;
import com.actolap.wse.model.User;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ServerCache {

	public static LoadingCache<String, Optional<UserDepartment>> userCache = CacheBuilder.newBuilder().maximumSize(1000)
			.expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<String, Optional<UserDepartment>>() {
				@Override
				public Optional<UserDepartment> load(String key) throws Exception {
					UserDepartment userDepartment = null;
					User user = UserDao.getById(key);
					if (user != null && user.getStatus() == UserStatus.ACTIVE) {
						Department department = DepartmentDao.getById(user.getDepartmentId());
						if (department != null && department.getStatus() == DepartmentStatus.ACTIVE) {
							userDepartment = new UserDepartment(user, department);
						}
					}
					if (userDepartment != null) {
						return Optional.of(userDepartment);
					} else {
						return Optional.absent();
					}
				}
			});

	public static void refreshUser(String userId) {
		try {
			userCache.invalidate(userId);
		} catch (Exception e) {
			// ignore
		}

	}

	public static void refreshDepartment(String departMentId) {
		try {
			List<User> users = UserDao.list(departMentId, null, null);
			for (User user : users) {
				userCache.invalidate(user.getId());
			}

		} catch (Exception e) {
			// ignore
		}

	}
}

