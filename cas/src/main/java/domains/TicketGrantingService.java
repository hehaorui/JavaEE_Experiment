package domains;

import java.sql.Timestamp;

public class TicketGrantingService {
	
	private String TGS;
	private User user;
	private Timestamp created=new Timestamp(System.currentTimeMillis());
	public String getTGS() {
		return TGS;
	}
	public void setTGS(String TGS) {
		this.TGS = TGS;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((TGS == null) ? 0 : TGS.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TicketGrantingService other = (TicketGrantingService) obj;
		if (TGS == null) {
			if (other.TGS != null)
				return false;
		} else if (!TGS.equals(other.TGS))
			return false;
		return true;
	}
	
}