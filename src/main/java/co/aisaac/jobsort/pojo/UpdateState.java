package co.aisaac.jobsort.pojo;

import java.util.Objects;

public class UpdateState {

	private long id;
	private String state;

	public UpdateState() {
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "UpdateState{" +
				"id=" + id +
				", state='" + state + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UpdateState that = (UpdateState) o;
		return id == that.id &&
				Objects.equals(state, that.state);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, state);
	}
}
