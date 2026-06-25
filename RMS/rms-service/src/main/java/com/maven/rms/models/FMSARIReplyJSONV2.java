package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.List;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARIReplyJSONV2{ 
	private String id;
	private Note note;
	private Integer rowNumber;
	private Amount Amout;
	private Balance Balance;
	private BillingPrinted BillingPrinted;
	private CreatedDateTime CreatedDateTime;
	private Currency Currency;
	private Customer Customer;
	private CustomerOrder CustomerOrder;
	@SerializedName("Date")
	private DateVal Date;
	private Description Description;
	private List<Details> Details;
	private DueDate DueDate;
	private Hold Hold;
	private LastModifiedDateTime LastModifiedDateTime;
	private LinkARAccount LinkARAccount;
	private LinkARSubAccount LinkARSubAccount;
	private LinkBranch LinkBranch;
	private PostPeriod PostPeriod;
	private Project Project;
	private ReferenceNbr ReferenceNbr;
	private Status Status;
	private TaxTotal TaxTotal;
	private Terms Terms;
	private Type Type;
	@SerializedName("_links")
	private links links;
	private custom custom;
	private document document;
	private List<files> files;
	
	@Getter
	@Setter
	public class Note{
		private String value;
	}
	
	@Getter
	@Setter
	public class Amount{
		private BigDecimal value;
	}
	@Getter
	@Setter
	public class Balance{
		private BigDecimal value;
	}
	@Getter
	@Setter
	public class BillingPrinted{
		private Boolean value;
	}
	@Getter
	@Setter
	public class CreatedDateTime{
		private String value;
	}
	@Getter
	@Setter
	public class Currency{
		private String value;
	}
	@Getter
	@Setter
	public class Customer{
		private String value;
	}
	@Getter
	@Setter
	public class CustomerOrder{
		private String value;
	}
	@Getter
	@Setter
	public class DateVal{
		private String value;
	}
	@Getter
	@Setter
	public class Description{
		private String value;
	}
	@Getter
	@Setter
	public class Details{
		private List<Detail> details;
		@Getter
		@Setter
		public class Detail{
			private Account Account;
			private Amount Amount;
			private Branch Branch;
			private DeferralCode DefferalCode;
			private DiscountAmount DiscountAmount;
			private ExtendedPrice ExtendedPrice;
			private InventoryID InventoryID;
			private LastModifiedDateTime LastModifiedDateTime;
			private LineNbr LineNbr;
			private ProjectTask ProjectTask;
			private Qty Qty;
			private Subaccount Subaccount;
			private TaxCategory TaxCategory;
			private TermEndDate TermEndDate;
			private TermStartDate TermStartDate;
			private TransactionDescription TransactionDescription;
			private UOM UOM;
			private UnitPrice UnitPrice;
			@SerializedName("_links")
			private links links;
			private custom custom;
			private List<files> files;
			private String id;
			private String note;
			private Integer rowNumber;
			
			@Getter
			@Setter
			public class Account{
				private String value;
			}
			@Getter
			@Setter
			public class Amount{
				private BigDecimal value;
			}
			@Getter
			@Setter
			public class Branch{
				private String value;
			}
			@Getter
			@Setter
			public class DeferralCode{
				private String value;
			}
			@Getter
			@Setter
			public class DiscountAmount{
				private BigDecimal value;
			}
			@Getter
			@Setter
			public class ExtendedPrice{
				private BigDecimal value;
			}
			@Getter
			@Setter
			public class InventoryID{
				private String value;
			}
			@Getter
			@Setter
			public class LastModifiedDateTime{
				private String value;
			}
			@Getter
			@Setter
			public class LineNbr{
				private Integer value;
			}
			@Getter
			@Setter
			public class ProjectTask{
				private String value;
			}
			@Getter
			@Setter
			public class Qty{
				private Double value;
			}
			@Getter
			@Setter
			public class Subaccount{
				private String value;
			}
			@Getter
			@Setter
			public class TaxCategory{
				private String value;
			}
			@Getter
			@Setter
			public class TermEndDate{
				private String value;
			}
			@Getter
			@Setter
			public class TermStartDate{
				private String value;
			}
			@Getter
			@Setter
			public class TransactionDescription{
				private String value;
			}
			@Getter
			@Setter
			public class UOM{
				private String value;
			}
			@Getter
			@Setter
			public class UnitPrice{
				private BigDecimal value;
			}
			@Getter
			@Setter
			public class links{
				private String filesput;
			}
			@Getter
			@Setter
			public class custom{
				private String value;
			}
			@Getter
			@Setter
			public class files{
				private String value;
			}			
		}
	}
	@Getter
	@Setter
	public class DueDate{
		private String value;
	}
	@Getter
	@Setter
	public class Hold{
		private Boolean value;
	}
	@Getter
	@Setter
	public class LastModifiedDateTime{
		private String value;
	}
	@Getter
	@Setter
	public class LinkARAccount{
		private String value;
	}
	@Getter
	@Setter
	public class LinkARSubAccount{
		private String value;
	}
	@Getter
	@Setter
	public class LinkBranch{
		private String value;
	}
	@Getter
	@Setter
	public class PostPeriod{
		private String value;
	}
	@Getter
	@Setter
	public class Project{
		private String value;
	}
	@Getter
	@Setter
	public class ReferenceNbr{
		private String value;
	}
	@Getter
	@Setter
	public class Status{
		private String value;
	}
	@Getter
	@Setter
	public class TaxTotal{
		private BigDecimal value;
	}
	@Getter
	@Setter
	public class Terms{
		private String value;
	}
	@Getter
	@Setter
	public class Type{
		private String value;
	}
	@Getter
	@Setter
	public class links{
		private String filesput;
		private String self;
	}
	@Getter
	@Setter
	public class custom{
		private CurrentDocument CurrentDocument;

		@Getter
		@Setter
		public class CurrentDocument{
			private AttributeGENPDF AttributeGENPDF;
			private AttributeSYSNAME AttributeSYSNAME;

			@Getter
			@Setter
			public class AttributeGENPDF{
				private String type;
				private Boolean value;
			}
			@Getter
			@Setter
			public class AttributeSYSNAME{
				private String type;
				private String value;
			}
		}
	}
	@Getter
	@Setter
	public class document{
		private String value;
	}
	@Getter
	@Setter
	public class files{
		private String value;
	}
}