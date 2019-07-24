package com.actolap.wse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import com.actolap.wse.cache.TournamentCache;
import com.actolap.wse.dao.WithdrawRequestDao;
import com.actolap.wse.dto.NotificationDto;
import com.actolap.wse.dto.PlayerTransactionDto;
import com.actolap.wse.model.WithdrawRequest;
import com.actolap.wse.model.player.Category;
import com.actolap.wse.model.player.PlayerClass;
import com.actolap.wse.model.player.PlayerTransaction;

public class PlayerUtils {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd, MMMM yyyy");

	
	//for Transaction history
	public static String buildTransactionMessageForSettings(PlayerTransaction transaction,
			List<PlayerTransactionDto> transections, String status, String type) {
		String message = null;
		String transactionType = null;
		String transactionStatus = null;
		String transactionId = null;
		switch (transaction.getType()) {
		case BUYIN:
			message = "You bought " + transaction.getChips() + " real chips";
			transactionType = "Deposit";
			transactionStatus = "Approved";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
					message, transactionType, transactionStatus, transactionId));
			if(type == null || type.equals("")) {
				if (transaction.getBonus() != null) {
					message = "You have earned " + transaction.getBonus() + " bonus chips";
					transactionType = "Locked Bonus";
					transactionStatus = "Approved";
					if (transaction.getDisplayTransaxId() != null)
						transactionId = transaction.getDisplayTransaxId();
					else
						transactionId = transaction.getId();
					if(status == null || status.equals("") || status.equals(transactionStatus))
						transections
							.add(new PlayerTransactionDto(transaction.getBonus(), sdf.format(transaction.getCreateTime()),
									message, transactionType, transactionStatus, transactionId));
				}
		}
			break;
			
		case CLASS_UPGRADE:
			transactionType = "Class Upgradation";
			transactionStatus = "Approved";
			if(transaction.getChips() == null)
				transaction.setChips(Long.parseLong("0"));
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if(status != null ) {
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus))) {
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
					message, transactionType, transactionStatus, transactionId));
			}
			}
			break;
		case UPGRADATION_FEE:
			transactionType = "Upgradation fee";
			transactionStatus = "Deducted";
			if(transaction.getChips() == null)
				transaction.setChips(Long.parseLong("0"));
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if(status != null ) {
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus))) {
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
					message, transactionType, transactionStatus, transactionId));
			}
			}
			break;
			
		case EARNED:
			if (transaction.getBonus() != null) {
				transactionType = "Locked Bonus";
				transactionStatus = "Approved";
				if (transaction.getDisplayTransaxId() != null)
					transactionId = transaction.getDisplayTransaxId();
				else
					transactionId = transaction.getId();
				message = "You have earned " + transaction.getBonus() + " bonus chips";
				if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
					transections
						.add(new PlayerTransactionDto(transaction.getBonus(), sdf.format(transaction.getCreateTime()),
								message, transactionType, transactionStatus, transactionId));
			} else if (transaction.getTournament() != null) {
				message = "You have earned " + transaction.getTournament() + " tournament points";
				transactionType = "Tournament Chips";
				transactionStatus = "Approved";
				if (transaction.getDisplayTransaxId() != null)
					transactionId = transaction.getDisplayTransaxId();
				else
					transactionId = transaction.getId();
					if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
						transections.add(
								new PlayerTransactionDto(transaction.getTournament(), sdf.format(transaction.getCreateTime()),
										message, transactionType, transactionStatus, transactionId));
			} else {
				message = "You have earned " + transaction.getVip() + " VIP points";
				transactionType = "Vip Points";
				transactionStatus = "Approved";
				if (transaction.getDisplayTransaxId() != null)
					transactionId = transaction.getDisplayTransaxId();
				else
					transactionId = transaction.getId();
				if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
					transections.add(new PlayerTransactionDto(transaction.getVip(), sdf.format(transaction.getCreateTime()),
							message, transactionType, transactionStatus, transactionId));
			}
			break;
		case REFUNDED:
			message = "Your real chips has been refunded";
			transactionType = "Chips Refund";
			transactionStatus = "Refunded";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
						message, transactionType, transactionStatus, transactionId));
			break;
		case WITHDRAWAL_REQUEST:
			message = "You have requested to " + transaction.getChips() + " withdraw real chips";
			transactionType = "Withdrawl";
			transactionStatus = "Pending";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
						message, transactionType, transactionStatus, transactionId));
			break;
			
		case COUPON_REIMBURSEMENT:
			if(transaction.getBonus() != null)
				message = "You have earned "+ transaction.getBonus()+" bonus points from coupon";
			else if(transaction.getTournamentChips() != null)
				message = "You have earned "+ transaction.getTournamentChips()+" tournament points from coupon";
			else if(transaction.getVip() != null)
				message = "You have earned "+ transaction.getVip()+" vip points from from coupon";
			break;
		case WITHDRAWAL_FAILED:
			message = "Withdrawal request for " + transaction.getChips() + " real chips has been declined";
			transactionType = "Withdrawl";
			transactionStatus = "Rejected";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
						message, transactionType, transactionStatus, transactionId));
			break;
		case WITHDRAW_REVERSED:
			message = "Withdrawal request for " + transaction.getChips() + " real chips has been reversed";
			transactionType = "Withdrawl";
			transactionStatus = "Reversed";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
						message, transactionType, transactionStatus, transactionId));
			break;
		case WITHDRAWN:
			message = "You have withdrawn real chips";
			transactionType = "Withdrawl";
			transactionStatus = "Approved";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus))) {
				WithdrawRequest withdrawRequest = WithdrawRequestDao.getByTrannxId(transaction.getDisplayTransaxId());
				if(withdrawRequest != null) {
					if(transaction.getFinalPaidWithdrawAmount() != null)
						 transections.add(new PlayerTransactionDto(withdrawRequest.getFinalPaidWithdrawAmount(), sdf.format(transaction.getCreateTime()),
								message, transactionType, transactionStatus, transactionId));}
				}
			 
			break;
		case TOURNAMENT:
			message = "You are registered for tournament league "
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			transactionType = "Tournament";
			transactionStatus = "Registered";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();

			if (transaction.getChips() != null)
				if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
					transections
							.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
									message, transactionType, transactionStatus, transactionId));
			else if (transaction.isTicket()) {

			}

			else {
				if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
					transections.add(new PlayerTransactionDto(transaction.getRealChips(), transaction.getTournamentChips(),
							sdf.format(transaction.getCreateTime()), message, transactionType, transactionStatus,
							transactionId));
			}
			break;
		case TOURNAMENT_REFUNDED:
			message = "Refunded from Tournament " + TournamentCache.getTournamentTitle(transaction.getRefId());
			transactionType = "Tournament";
			transactionStatus = "Refunded";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
						message, transactionType, transactionStatus, transactionId));
			break;
			
		case TOURNAMENT_REVERSE:
			message = "Unregister from Tournament " + TournamentCache.getTournamentTitle(transaction.getRefId());
			transactionType = "Tournament";
			transactionStatus = "Reversed";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if(transaction.getRealChips() != null)
				if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
					transections.add(new PlayerTransactionDto(transaction.getRealChips(), sdf.format(transaction.getCreateTime()),
							message, transactionType, transactionStatus, transactionId));
			else {
				if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
					transections.add(new PlayerTransactionDto(0, sdf.format(transaction.getCreateTime()),
							message, transactionType, transactionStatus, transactionId));
			}
				
			break;
		case TOURNAMENT_WON:
			message = "Winning amount deposited from Tournament "
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			transactionType = "Tournament";
			transactionStatus = "Won";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
						message, transactionType, transactionStatus, transactionId));
			break;
		case TOURNAMENT_REBUY:
			message = "You re-buy real chips from Tournament "
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			transactionType = "Tournament";
			transactionStatus = "Rebuy";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if((status != null && status.equals("")) || (status != null && status.equals(transactionStatus)))
				transections.add(new PlayerTransactionDto(transaction.getChips(), sdf.format(transaction.getCreateTime()),
						message, transactionType, transactionStatus, transactionId));
			break;
		/*case CLASS_UPGRADE:
			message = "You have been upgraded from " + Utils.getClassLabel(transaction.getOldClass()) + " to "
					+ Utils.getClassLabel(transaction.getUpdateClass()) + " Class";
			transactionType = "Playerclass";
			transactionStatus = "Upgraded";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if(status == null || status.equals("") || status.equals(transactionStatus))
				transections.add(new PlayerTransactionDto(0, sdf.format(transaction.getCreateTime()), message,
						transactionType, transactionStatus, transactionId));
			break;
		case CLASS_DOWNGRADE:
			message = "You have been downgraded from " + Utils.getClassLabel(transaction.getOldClass()) + " to "
					+ Utils.getClassLabel(transaction.getUpdateClass()) + " Class";
			transactionType = "Playerclass";
			transactionStatus = "Downgraded";
			if (transaction.getDisplayTransaxId() != null)
				transactionId = transaction.getDisplayTransaxId();
			else
				transactionId = transaction.getId();
			if(status == null || status.equals("") || status.equals(transactionStatus))
				transections.add(new PlayerTransactionDto(0, sdf.format(transaction.getCreateTime()), message,
						transactionType, transactionStatus, transactionId));
			break;*/
		default:
			break;
		}

		return message;
	}

	public static List<String> buildTransactionMsgforDashboard(PlayerTransaction transaction) {
		List<String> notifications = new ArrayList<>();
		String message = null;
		switch (transaction.getType()) {
		case BUYIN:
			message = "You bought " + transaction.getChips() + " real chips";
			notifications.add(message);
			if (transaction.getBonus() != null) {
				message = "You have earned " + transaction.getBonus() + " bonus chips";
				notifications.add(message);
			}
			break;
		case EARNED:
			if (transaction.getBonus() != null)
				message = "You have earned " + transaction.getBonus() + " bonus chips";
			else if (transaction.getTournament() != null)
				message = "You have earned " + transaction.getTournamentChips() + " tournament points";
			else
				message = "You have earned " + transaction.getVip() + " vip points";
			notifications.add(message);
			break;
		case REFUNDED:
			message = "Your " + transaction.getChips() + " real chips has been refunded";
			notifications.add(message);
			break;
		case COUPON_REIMBURSEMENT:
			if(transaction.getBonus()  != null && transaction.getBonus() > 0)
				message = "You have earned "+ transaction.getBonus()+" bonus points from coupon";
			else if(transaction.getTournamentChips() != null && transaction.getTournamentChips() > 0)
				message = "You have earned "+ transaction.getTournamentChips()+" tournament points from coupon";
			else if(transaction.getVip() != null && transaction.getVip() > 0)
				message = "You have earned "+ transaction.getVip()+" vip points from from coupon";
			notifications.add(message);
			break;
		case WITHDRAWN:
			message = "Your withdrawal of " + transaction.getChips() + " real chips has been completed ";
			notifications.add(message);
			break;
		case WITHDRAWAL_FAILED:
			message = "Your withdrawal request of " + transaction.getChips() + " real chips has been declined";
			notifications.add(message);
			break;
		case WITHDRAWAL_REQUEST:
			message = "Your withdrawal request of " + transaction.getChips() + " real chips has been placed";
			notifications.add(message);
			break;
			
		case ACCOUNT_APPROVED:
			message = "Your account number  " + transaction.getAccountNumber() + " has been approved";
			notifications.add(message);
			break;
		case ACCOUNT_REJECTED:
			message = "Your account number " + transaction.getAccountNumber() + " has been rejected";
			//notifications.add(NotificationDto.build(message, transaction));
			notifications.add(message);
			break;
		case TOURNAMENT:
			if (transaction.getChips() != null && transaction.getChips() > 0)
				message = "You are registered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for " + transaction.getChips()
						+ " real chips ";
			else if (transaction.getRealChips() != null || transaction.getRealChips() > 0 && transaction.getTournamentChips() != null && transaction.getTournamentChips() > 0)
				message = "You are registered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips " + transaction.getTournamentChips()
						+ " tournamentChips";
			else if (transaction.getRealChips() != null && transaction.getRealChips() > 0)
				message = "You are registered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips ";
			else if (transaction.getTournamentChips() != null && transaction.getTournamentChips() > 0)
				message = "You are registered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getTournamentChips() + " tournament chips ";
			else
				message = "You are registered for tournament league from Tournament Ticket ";
			notifications.add(message);

			break;
			
		case TOURNAMENT_REVERSE:
			if (transaction.getChips() != null)
				message = "You have unregistered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for " + transaction.getChips()
						+ " real chips ";
			else if (transaction.getRealChips() > 0 && transaction.getTournamentChips() > 0)
				message = "You have unregistered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips " + transaction.getTournamentChips()
						+ " tournamentChips";
			else if (transaction.getRealChips() > 0)
				message = "You have unregistered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips ";
			else if (transaction.getTournamentChips() > 0)
				message = "You have unregistered for tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getTournamentChips() + " tournament chips ";
			else
				message = "You are unregistered for tournament league from Tournament Ticket ";
			notifications.add(message);

			break;
		case TOURNAMENT_REFUNDED:
			if (transaction.getRefId() != null)
				message = "Your " + transaction.getChips() + " real chips has been refunded from "
						+ TournamentCache.getTournamentTitle(transaction.getRefId());
			notifications.add(message);
			break;
		case TOURNAMENT_WON:
			message = "Your winning amount of " + transaction.getChips() + " real chips has been deposited from "
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			notifications.add(message);
			break;
		case TOURNAMENT_REBUY:
			message = "You re-buy " + transaction.getChips() + " real chips from "
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			notifications.add(message);
			break;
		case CLASS_UPGRADE:
			message = "You have been upgraded from " + Utils.getClassLabel(transaction.getOldClass()) + " to "
					+ Utils.getClassLabel(transaction.getUpdateClass()) + " Class";
			notifications.add(message);
			break;
		case CLASS_DOWNGRADE:
			message = "You have been downgraded from " + Utils.getClassLabel(transaction.getOldClass()) + " to "
					+ Utils.getClassLabel(transaction.getUpdateClass()) + " class";
			notifications.add(message);
			break;
		}
		return notifications;
	}

	public static void buildTransactionMsgforDashboard(PlayerTransaction transaction,
			List<NotificationDto> notifications) { 
		String message = null; 		
		switch (transaction.getType()) { 
		case BUYIN: 
			if (transaction.getBonus() != null) {
				message = "You have earned " + transaction.getBonus() + " bonus chips";
				notifications.add(NotificationDto.build(message, transaction));
			}
			message = "Your purchase of "+transaction.getChips()+ " real chips is successful";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case EARNED:
			if (transaction.getBonus() != null)
				message = "You have earned " + transaction.getBonus() + " bonus chips";
			else if (transaction.getTournament() != null)
				message = "You have earned " + transaction.getTournament() + " bonus chips";
			else
				message = "You have earned " + transaction.getVip() + " bonus chips";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case REFUNDED:
			message = "Your " + transaction.getChips() + " real chips has been refunded";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case PLAYER_ACHIEVEMENT:
			if(transaction.getRealChips() != null && transaction.getRealChips() > 0 && transaction.getAchievementLevel() != null && transaction.getAchievementName() != null)
				message = "You achieved "+transaction.getAchievementLevel()+" of " +transaction.getAchievementName()+" your account is credited with  " + transaction.getRealChips() + " real chips";
			else if(transaction.getBonus() != null && transaction.getBonus() > 0 && transaction.getAchievementLevel() != null && transaction.getAchievementName() != null)
				message = "You achieved "+transaction.getAchievementLevel()+" of " +transaction.getAchievementName()+" your account is credited with  " + transaction.getBonus() + " bonus chips";
			else if(transaction.getTournamentChips() != null && transaction.getTournamentChips() > 0 && transaction.getAchievementLevel() != null && transaction.getAchievementName() != null)
				message = "You achieved "+transaction.getAchievementLevel()+" of " +transaction.getAchievementName()+" your account is credited with  " + transaction.getTournamentChips() + " tournament points";
			else if(transaction.getVip() != null && transaction.getVip() > 0 && transaction.getAchievementLevel() != null && transaction.getAchievementName() != null)
				message = "You achieved "+transaction.getAchievementLevel()+" of " +transaction.getAchievementName()+" your account is credited with  " + transaction.getVip() + " vip points";
			
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case LYVE_WALLET_TO_LYVE_CHIPS :
			if(transaction.getRealChips() != null)
				message = "you have transferred Rs."+transaction.getRealChips()+" from Lyve wallet to Lyve Chips";
				notifications.add(NotificationDto.build(message, transaction));
			break;
		case LYVE_CHIPS_TO_LYVE_WALLET :
			if(transaction.getRealChips() != null)
				message = "you have transferred Lyve Chips Rs."+transaction.getRealChips();
			if(transaction.getWinningAmount() != null && transaction.getTds() != null && transaction.getWinningAmount() > 9999)
				message = message +", TDS applicable Rs."+transaction.getTds()+ " on winning amount Rs."+transaction.getWinningAmount();
			message = message+" to your Lyve Wallet";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case ADVANCE_BONUS :
			if(transaction.getRealChips() != null)
				message = "Rs. "+transaction.getRealChips() + " bonus has been deducted from your wallet";
				notifications.add(NotificationDto.build(message, transaction));
			break;
		case security_pin:
			message = "4 digit security pin has sent to your registered email id";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case WITHDRAWN:
			WithdrawRequest withdrawRequest = WithdrawRequestDao.getByTrannxId(transaction.getDisplayTransaxId());
			if(withdrawRequest != null) {
				message = "Withdrawal request of " + Long.toString(withdrawRequest.getFinalPaidWithdrawAmount()) + " real chips has been completed ";
			}
			
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case WITHDRAWAL_FAILED:
			message = "Your withdrawal request of " + transaction.getChips() + " real chips has been declined";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case WITHDRAWAL_REQUEST:
			message = "Your withdrawal request of " + transaction.getChips() + " real chips has been placed";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case ACCOUNT_APPROVED:
			message = "Your account number  " + transaction.getAccountNumber() + " has been approved";
			notifications.add(NotificationDto.build(message, transaction));
			//notifications.add(message);
			break;
		case ACCOUNT_REJECTED:
			message = "Your account number " + transaction.getAccountNumber() + " has been rejected";
			notifications.add(NotificationDto.build(message, transaction));
			//notifications.add(message);
			break;
		case TOURNAMENT:
			if (transaction.getChips() != null)
				message = "You are registered for tournament league "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for " + transaction.getChips()
						+ " real chips ";
			else if (transaction.getRealChips() != null && transaction.getTournamentChips() != null)
				message = "You are registered for tournament league "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips and " + transaction.getTournamentChips()
						+ " tournament Chips";
			else if (transaction.getTournamentChips() != null)
				message = "You are registered for tournament league "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getTournamentChips() + " tournament chips ";
			else if (transaction.getRealChips() != null)
				message = "You are registered for tournament league "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips ";
			else 
				message = "You are registered for tournament league "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " from Tournament Ticket ";
			notifications.add(NotificationDto.build(message, transaction));
			break;
			
		case TOURNAMENT_REVERSE:
			if (transaction.getRealChips() > 0 && transaction.getTournamentChips() > 0)
				message = "You have unregistered from tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips " + transaction.getTournamentChips()
						+ " tournament Chips";
			else if (transaction.getChips() != null)
				message = "You have unregistered from tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for " + transaction.getChips()
						+ " real chips ";
			
			else if (transaction.getRealChips() > 0)
				message = "You have unregistered from tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getRealChips() + " real chips ";
			else if (transaction.getTournamentChips() > 0)
				message = "You have unregistered from tournament league  "
						+ TournamentCache.getTournamentTitle(transaction.getRefId()) + " for "
						+ transaction.getTournamentChips() + " tournament chips ";
			else
				message = "You are unregistered from tournament league from Tournament Ticket ";
			notifications.add(NotificationDto.build(message, transaction));

			break;
		case COUPON_REIMBURSEMENT:
			if (transaction.getTournamentChips() != null) {
				if(!transaction.isSignUpCoupon())
					message = "You have earned  " + transaction.getTournamentChips() + " tournament points  from coupon";
				else
					message = "Yay! Sign Up Bonus of "+transaction.getTournamentChips()+ " tournament points has been credited in your account. Enjoy Playing!";
			}
			else if (transaction.getVip() != null) {
				if(!transaction.isSignUpCoupon())
					message = "You have earned " + transaction.getVip() + " vip points from coupon";
				else
					message = "Yay! Sign Up Bonus of "+transaction.getVip()+ " VIP points has been credited in your account. Enjoy Playing!";
			}
			else if(transaction.getBonus() != null) {
				if(!transaction.isSignUpCoupon())
					message = "You have earned " + transaction.getBonus() + " bonus points from coupon";
				else
					message = "Yay! Sign Up Bonus of "+transaction.getBonus()+ " bonus points has been credited in your account. Enjoy Playing!";
			}
			else if(transaction.getRealChips() != null) {
				if(!transaction.isSignUpCoupon())
					message = "You have earned " + transaction.getRealChips() + " real chips from coupon";
				else
					message = "Yay! Sign Up Bonus of "+transaction.getRealChips()+ " real chips has been credited in your account. Enjoy Playing!";
			}
			notifications.add(NotificationDto.build(message, transaction));
			break;

		case TOURNAMENT_REFUNDED:
			if(transaction.getChips() != null && transaction.getTournamentChips() != null) {
			message = "Your " + transaction.getChips() + " real chips and "+transaction.getTournamentChips()+" tournament chips has  been refunded from "
			
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			} else if(transaction.getChips() != null) {
			message = "Your " + transaction.getChips() + " real chips has  been refunded from "
			
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			} else if(transaction.getTournamentChips() != null) {
			message = "Your " + transaction.getTournamentChips() + " tournament chips has  been refunded from "
			
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			}
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case TOURNAMENT_WON:
			message = "Your winning amount of " + transaction.getChips() + " real chips has been deposited from "
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case TOURNAMENT_REBUY:
			message = "You re-buy " + transaction.getChips() + " real chips from "
					+ TournamentCache.getTournamentTitle(transaction.getRefId());
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case PDF_DOWNLOAD:
			message = "Thank you for downloading PDF worth Rs. "+transaction.getRealChips()+ " from lyvegames.";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case CLASS_UPGRADE:
			message = "You have been upgraded from " + Utils.getClassLabel(transaction.getOldClass()) + " to "
					+ Utils.getClassLabel(transaction.getUpdateClass()) + " Class";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case CLASS_DOWNGRADE:
			message = "You have been downgraded from " + Utils.getClassLabel(transaction.getOldClass()) + " to "
					+ Utils.getClassLabel(transaction.getUpdateClass()) + " Class";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		case CHIPS_CONVERSION:
			message = "Your " + transaction.getVip() + " VIP Points has been converted to " + (transaction.getChips())
					+ " Real Chips";
			notifications.add(NotificationDto.build(message, transaction));
			break; 
		case DOCUMENT_REJECTED: 
			message = "Your " + transaction.getDocType() + " has been Rejected please upload valid documents";
			notifications.add(NotificationDto.build(message, transaction));
			break;	
		case DOCUMENT_APPROVED: 
			message = "Your " + transaction.getDocType() + " has been Approved";
			notifications.add(NotificationDto.build(message, transaction));
			break;	
		case REJECTED: 
			message = "Your " + transaction.getDocType() + " has been Rejected please upload valid documents";
			notifications.add(NotificationDto.build(message, transaction));
			break;
		default:
			break;	
		} 
	} 
	
	public static List<String> buildPlayerClassOfferMsg(Category category) {
		List<String> offers = new ArrayList<>();
		switch (category.getTitle()) {
		case CRYSTAL:
			offers.addAll(buildOfferMsgList(category));
			offers.add("Applicable Upgradation fees of ₹" + category.getUpgradationFee() + " will be levied.");
			break;
		case PEARL:
			offers.addAll(buildOfferMsgList(category));
			offers.add("Applicable Upgradation fees of ₹" + category.getUpgradationFee() + " will be levied.");
			break;
		case TOPAZ:
			offers.addAll(buildOfferMsgList(category));
			offers.add("Applicable Upgradation fees of ₹" + category.getUpgradationFee() + " will be levied.");
			break;
		case SAPPHIRE:
			offers.addAll(buildOfferMsgList(category));
			offers.add("Applicable Upgradation fees of ₹" + category.getUpgradationFee() + " will be levied.");
			break;
		case DIAMOND:
			offers.addAll(buildOfferMsgList(category));
			offers.add("Applicable Upgradation fees of ₹" + category.getUpgradationFee() + " will be levied.");
			break;
		case RUBY:
			offers.addAll(buildOfferMsgList(category));
			offers.add("You are entitled to refund of TDS deducted upto " + category.getTdsRefundPert()
					+ "% of the VIP points surrendered or the amount of TDS whichever is higher.");
			offers.add("Applicable Upgradation fees of ₹" + category.getUpgradationFee() + " will be levied.");
			break;
		case OPAL:
			offers.addAll(buildOfferMsgList(category));
			offers.add("You are entitled to refund of TDS deducted upto " + category.getTdsRefundPert()
					+ "% of the VIP points surrendered or the amount of TDS whichever is higher.");
			offers.add("Applicable Upgradation fees of ₹" + category.getUpgradationFee() + " will be levied.");
			break;
		}
		return offers;
	}

	public static List<String> buildOfferMsgList(Category category) {
		List<String> offers = new ArrayList<>();
		if (category.getTitle().equals(PlayerClass.CRYSTAL)) {
			offers.add("This is the default club allotted when you Sign-in at LYVE Games.");
			offers.add("There are no minimum points to be retained for this category.");
			offers.add(
					"You will be allotted " + category.getVipPointsPert() + "% VIP Points on net rake generated.");
			offers.add("You are entitled to get Bonus of " + category.getBonusChipsPert() + "% on every Buy-in.");
			offers.add("The Bonus Chips allotted will have expired in " + category.getTenureDays() + " days from the date of issuance.");
		} else {
			offers.add(category.getEveryMonthPoints() + " VIP points need to be maintained to retain your club.");
			offers.add(
					"You will be allotted " + category.getVipPointsPert() + "% VIP Points on net rake generated.");
			offers.add("You are entitled to get Bonus of " + category.getBonusChipsPert() + "% on every Buy-in.");
			offers.add("The Bonus Chips allotted will have expired in " + category.getTenureDays() + " days from the date of issuance.");
			/*offers.add("Special tournament discount of " + category.getTournamentTicketDiscount() + "%.");*/
		}
		return offers;
	}
	
	
	
}

