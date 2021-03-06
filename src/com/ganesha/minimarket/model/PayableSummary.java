package com.ganesha.minimarket.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.model.LogableEntity;
import com.ganesha.model.Trackable;

@Entity
@Table(name = "PAYABLE_SUMMARIES")
public class PayableSummary extends Trackable implements LogableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "CLIENT_ID", nullable = false, unique = true)
	private Integer clientId;

	@Column(name = "REMAINING_AMOUNT", nullable = false)
	private BigDecimal remainingAmount;

	@Column(name = "LAST_PAYABLE_TRANSACTION_ID", nullable = false)
	private Integer lastPayableTransactionId;

	public Integer getClientId() {
		return clientId;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public Integer getLastPayableTransactionId() {
		return lastPayableTransactionId;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLastPayableTransactionId(Integer lastPayableTransactionId) {
		this.lastPayableTransactionId = lastPayableTransactionId;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
}
