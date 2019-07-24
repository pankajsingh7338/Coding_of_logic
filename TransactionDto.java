package com.actolap.wse.dto;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerTransaction;
import com.actolap.wse.model.player.PlayerTransaction.TransactionType;
import com.actolap.wse.model.promotion.RedemptionType;

public class TransactionDto { 

	private String id;
	private Long amount;
	private String date;
	private String transactionId;
	private TransactionType type;
	private Long chips; 
	private Long bonus; 
	private Long tournament; 
	private Long vip; 
	private RedemptionType showType; 
	private String gameName; 
	private String couponCode; 
	private Long tds; 
	private Long withdrawCharge; 

	public TransactionDto(PlayerTransaction playerTransaction) { 
		if (playerTransaction.getDisplayTransaxId() != null) 
			this.transactionId = playerTransaction.getDisplayTransaxId(); 
		else 
			this.transactionId = playerTransaction.getId(); 
		this.type = playerTransaction.getType(); 
		if (playerTransaction.getCouponCode() != null) { 
			this.couponCode = playerTransaction.getCouponCode(); 
		} 
		String UtcToIst = Utils.utcToIst(playerTransaction.getCreateTime()); 
		this.date = UtcToIst; 
		if (playerTransaction.getType().equals(TransactionType.COUPON_REIMBURSEMENT)
				|| playerTransaction.getType().equals(TransactionType.COUPON_REVERSED)) { 
			if (playerTransaction.getTournamentChips() != null) { 
				this.amount = playerTransaction.getTournamentChips(); 
				this.showType = RedemptionType.TOURNAMENT_POINTS; 
			} else if (playerTransaction.getTournament() != null) { 
				this.amount = playerTransaction.getTournament(); 
				this.showType = RedemptionType.TOURNAMENT_POINTS; 
			} else if (playerTransaction.getVip() != null) { 
				this.amount = playerTransaction.getVip(); 
				this.showType = RedemptionType.VIP_POINTS; 
			} else if (playerTransaction.getBonus() != null) { 
				this.amount = playerTransaction.getBonus(); 
				this.showType = RedemptionType.BONUS_POINTS; 
			} else if (playerTransaction.getRealChips() != null) { 
				this.amount = playerTransaction.getRealChips(); 
				this.showType = RedemptionType.CASH; 
			} 
		} else if (playerTransaction.getType().equals(TransactionType.CHIPS_CONVERSION)) { 
			this.amount = playerTransaction.getBonus(); 
			this.showType = RedemptionType.REAL_CHIPS; 
		} else if (playerTransaction.getBonus() != null) { 
			this.amount = playerTransaction.getBonus(); 
			this.showType = RedemptionType.BONUS; 
		} else if (playerTransaction.getVip() != null) { 
			this.amount = playerTransaction.getVip(); 
			this.showType = RedemptionType.VIP; 
		} else if (playerTransaction.getChips() != null && playerTransaction.getFinalPaidWithdrawAmount() != null) { 
			this.amount = Long.parseLong(playerTransaction.getFinalPaidWithdrawAmount()); 
			this.tds = playerTransaction.getTds(); 
			this.showType = RedemptionType.BANK; 
			this.withdrawCharge = playerTransaction.getCharges(); 
		} else if (playerTransaction.getChips() != null) { 
			this.amount = playerTransaction.getChips(); 
			this.showType = RedemptionType.BANK; 
		} else if (playerTransaction.getTournament() != null) { 
			this.amount = playerTransaction.getTournament(); 
			this.showType = RedemptionType.TOURNAMENT_POINTS; 
		} else if (playerTransaction.getRealChips() != null) { 
			this.amount = playerTransaction.getRealChips(); 
			this.showType = RedemptionType.CASH; 
		} else if (playerTransaction != null) { 
			String playerId = playerTransaction.getPid(); 
			Player player = PlayerDao.getById(playerId); 
			this.gameName = player.getGameName(); 
			this.id = player.getId(); 
		} 
	} 

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Long getChips() {
		return chips;
	}

	public void setChips(Long chips) {
		this.chips = chips;
	}

	public Long getBonus() {
		return bonus;
	}

	public void setBonus(Long bonus) {
		this.bonus = bonus;
	}

	public Long getTournament() {
		return tournament;
	}

	public void setTournament(Long tournament) {
		this.tournament = tournament;
	}

	public Long getVip() {
		return vip;
	}

	public void setVip(Long vip) {
		this.vip = vip;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public RedemptionType getShowType() {
		return showType;
	}

	public void setShowType(RedemptionType showType) {
		this.showType = showType;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Long getTds() {
		return tds;
	}

	public void setTds(Long tds) {
		this.tds = tds; 
	}

	public Long getWithdrawCharge() {
		return withdrawCharge;
	}

	public void setWithdrawCharge(Long withdrawCharge) {
		this.withdrawCharge = withdrawCharge;
	}

}

