/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * Modified by @author nmatovu last on Sep 29, 2016
 *
 */
public class CenterUserRole implements Serializable {
	private static final long serialVersionUID = 9180666672990063594L;
	@Expose
	private Long id;

	private CenterRole role;

	@Expose
	private User user;

	public CenterUserRole() {
	}

	public CenterUserRole(CenterRole roles, User users) {
		this.role = roles;
		this.user = users;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		CenterUserRole other = (CenterUserRole) obj;
		if (role == null) {
			if (other.role != null) {
				return false;
			}
		} else if (!role.getId().equals(other.getRole().getId())) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.getUser())) {
			return false;
		}
		return true;
	}

	public Long getId() {
		return this.id;
	}

	public CenterRole getRole() {
		return this.role;
	}

	public User getUser() {
		return this.user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRole(CenterRole roles) {
		this.role = roles;
	}

	public void setUser(User users) {
		this.user = users;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
