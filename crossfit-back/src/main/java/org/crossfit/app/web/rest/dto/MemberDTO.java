package org.crossfit.app.web.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.Title;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.validator.constraints.Email;
import org.joda.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class MemberDTO {

	private static final long serialVersionUID = 1L;
	public static final int PASSWORD_MIN_LENGTH = 5;
	public static final int PASSWORD_MAX_LENGTH = 100;
    
	public static final Function<Member, MemberDTO> MAPPER =  (member)-> {
		MemberDTO dto = new MemberDTO();
		dto.setId(member.getId());
		dto.setUuid(member.getUuid());
		dto.setFirstName(member.getFirstName());
		dto.setLastName(member.getLastName());
		dto.setNickName(member.getNickName());
		dto.setTitle(member.getTitle());
		dto.setAddress(member.getAddress());
		dto.setZipCode(member.getZipCode());
		dto.setCity(member.getCity());
		
		dto.setEnabled(member.isEnabled());
		dto.setLangKey(member.getLangKey());
		dto.setLocked(member.isLocked());
		dto.setCardUuid(member.getCardUuid());
		dto.setEmail(member.getLogin());
		dto.setTelephonNumber(member.getTelephonNumber());
		
		dto.setGivenMedicalCertificate(member.hasGivenMedicalCertificate());
		dto.setMedicalCertificateDate(member.getMedicalCertificateDate());
		dto.setComments(member.getComments());
		dto.setNumber(member.getNumber());
         
		return dto;
	};
	public static final Function<Member, MemberDTO> CONVERTER = (m) ->{return MAPPER.apply(m);};

	
    private Long id;

    @NotNull
    private Title title;

    @NotNull
    @Size(max = 50)
    private String firstName;

    @NotNull
    @Size(max = 50)
    private String lastName;
    
    @Size(max = 50)
    private String nickName;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String zipCode;
    
    @Size(max = 100)
    private String city;
    
    @Size(max = 32)
    private String telephonNumber;

    @Size(max = 255)
    private String cardUuid;

    private String uuid;
    
    
    @NotNull
    @Email
    @Size(max = 100)
    private String email;
    
    private boolean locked;
    
    private boolean enabled;

    @Size(min = 2, max = 5)
    private String langKey;

    private List<String> roles;

    private List<SubscriptionDTO> subscriptions = new ArrayList<>();
    private List<MembershipRules> rules = new ArrayList<>();


    private boolean givenMedicalCertificate;

	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate medicalCertificateDate;

    @Size(max = 255)
    private String comments;
    
    @Size(max = 36)
    private String number;
    
    
    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLogin(){
    	return this.email;
    }

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCardUuid() {
		return cardUuid;
	}

	public void setCardUuid(String cardUuid) {
		this.cardUuid = cardUuid;
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Title getTitle() {
		return title;
	}


	public void setTitle(Title title) {
		this.title = title;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getZipCode() {
		return zipCode;
	}


	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getTelephonNumber() {
		return telephonNumber;
	}


	public void setTelephonNumber(String telephonNumber) {
		this.telephonNumber = telephonNumber;
	}

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public boolean isLocked() {
		return locked;
	}


	public void setLocked(boolean locked) {
		this.locked = locked;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public List<SubscriptionDTO> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<SubscriptionDTO> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public List<MembershipRules> getRules() {
		return rules;
	}

	public void setRules(List<MembershipRules> rules) {
		this.rules = rules;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public boolean isGivenMedicalCertificate() {
		return givenMedicalCertificate;
	}

	public void setGivenMedicalCertificate(boolean givenMedicalCertificate) {
		this.givenMedicalCertificate = givenMedicalCertificate;
	}

	public LocalDate getMedicalCertificateDate() {
		return medicalCertificateDate;
	}

	public void setMedicalCertificateDate(LocalDate medicalCertificateDate) {
		this.medicalCertificateDate = medicalCertificateDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "MemberDTO [id=" + id + ", title=" + title + ", firstName="
				+ firstName + ", lastName=" + lastName + ", address=" + address
				+ ", zipCode=" + zipCode + ", city=" + city
				+ ", telephonNumber=" + telephonNumber + ", email=" + email
				+ ", locked=" + locked + ", enabled=" + enabled + ", langKey="
				+ langKey + ", subscriptions=" + subscriptions + "]";
	}

	
	
}
