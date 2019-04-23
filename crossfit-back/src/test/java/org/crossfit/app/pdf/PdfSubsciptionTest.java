package org.crossfit.app.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.SubscriptionDirectDebit;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.enumeration.Title;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.PdfSubscription;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.xmp.XMPException;

/**
 * Test class for the UserResource REST controller.
 *
 * @see MemberService
 */
public class PdfSubsciptionTest {


    public static final void main(String args[]) throws IOException, DocumentException, ParserConfigurationException, XMPException, SAXException, ParseException, TransformerException {

        PdfSubscription.SubcriptionLegalText legalText = new PdfSubscription.SubcriptionLegalText();
        legalText.logoUrl = "http://www.crossfit-nancy.fr/img/logo_web.png";
        legalText.designationBeneficiaireText = "KOOPER inc, Société par Actions Simplifiée ayant son siège au 24/26 Boulevard du 26ème RI – 54 000 NANCY, au capital de 7 500 euros, immatriculée au RCS NANCY - SIRET 820 035 004 00010 - APE/NAF 8551Z. Personne morale représentant l’affiliation « CROSSFIT NANCY », Ci-après désignée « KOOPER inc - CROSSFIT NANCY Affiliate », d’autre part, S’établit un lien de droit de par la signature du présent document. \n" +
                "Tout litige entre les parties signataires sera réglé directement entre elles conformément à l’article 1165 du Code Civil.";
        
        legalText.cgvText = "KOOPER inc - CROSSFIT NANCY Affiliate se réserve le droit de modifier ses conditions générales de vente à tout moment. Les conditions générales de vente applicables sont celles en vigueur à la date de conclusion du contrat d’adhésion par l’adhérent.";
        legalText.cgvs = Arrays.asList(
"I. Objet du Contrat : Après avoir visité les installations, acté des horaires d’ouverture de la salle et pris connaissance des prestations proposées et du règlement intérieur, l’adhérent déclare souscrire auprès de l’établissement CROSSFIT NANCY un contrat d’adhésion nominatif et incessible, afin de bénéficier de ses prestations.",
"II. Services compris : Toutes activités de cours collectifs à thème, en salle ou en extérieur selon le planning de l’année en cours (horaires des cours susceptibles d’être modifiés durant l’année).",
"III. Prix : Le prix de vente (avec réductions éventuelles) des abonnements et des entrées individuelles à la salle est celui en vigueur au jour de la conclusion de la vente. Les prix sont affichés en permanence au sein des locaux et peuvent être communiqués à tout moment sur simple demande de l’adhérent.  En cas de hausse des prix postérieure à la vente d’une entrée individuelle ou de la souscription d’un abonnement, KOOPER Inc – CROSSFIT NANCY Affiliate s’engage à appliquer les tarifs en vigueur au jour de la passation de la commande ou de l’adhésion.",
"IV. Services hors abonnement : L’accès à des cours  individuels/séances  de  coaching  personnalisé  n’est  pas  compris  dans l’abonnement. L’adhérent devra s’en acquitter en sus auprès du prestataire concerné. La validité de cette prestation  sera limitée à la date de péremption du contrat de l’adhérent. La liste des services compris dans le contrat ou non, peut     faire l’objet de modifications à l’initiative de la salle CROSSFIT NANCY, dans les conditions de l’article R132.2 du Code de la Consommation. Certains types d’abonnement pourront faire l’objet de restrictions d’horaires, prestations qui seront stipulées dans les observations. Un complément financier pourra être demandé dans le cadre de la création d’activités ou d’horaires supplémentaires ultérieurement à la date de signature du présent contrat.",

"V. Paiement : Carte d’abonnement : L’adhérent règle à son inscription le montant forfaitaire en vigueur de la carte d’adhérent ainsi que la première prestation, payables le jour même, sans escompte, lui ouvrant droit à une adhésion à la formule de son choix. Aucun remboursement a posteriori ne pourra être effectué, dans la mesure où l’abonnement a été souscrit suite à une démarche volontaire de la part de l’abonné.  Abonnement mensuel : Aucune suspension d’abonnement ne pourra être acceptée. En cas de non-paiement de deux mensualités successives, cet abonnement sera considéré comme résilié de plein droit par KOOPER inc - CROSSFIT NANCY Affiliate. dans ce cas, l’accès à la salle et la pratique des activités ne seront plus autorisés à l’adhérent, qui perdra le bénéfice de sa carte d’abonnement et de reconduction, et ne pourra prétendre à aucun remboursement.  Les factures émises par KOOPER inc - CROSSFIT NANCY Affiliate. sont payables dès réception, par prélèvement automatique récurrent, ou par virement, carte bancaire, ou chèque, au siège social de KOOPER Inc. Aucun escompte n’est consenti pour paiement anticipé. Le montant des factures ne peut en aucun cas être minoré par l’adhérent des sommes qui peuvent lui  être éventuellement dues par KOOPER Inc. Toute facture non payée à son échéance, se verra majorée de plein droit et sans mise en demeure, d’intérêts conventionnels de retard de trois fois le taux d’intérêt légal, tout mois entamé étant dû. de plus, tout adhérent professionnel, en situation de retard ou de non-paiement, sera de plein droit débiteur dès le lendemain de l’échéance, d’une indemnité forfaitaire pour frais de recouvrement de 40 €, conformément à l’article d 441-5 du Code de commerce. Indépendamment de ce qui précède, KOOPER Inc. se réserve le droit sans que l’adhérent ne puisse demander quelque indemnisation que ce soit de ce chef, d‘interrompre l’abonnement jusqu’au paiement de la ou des factures échues, non payées en ce comprises les majorations de ces dernières. Tous litiges d’ordre judiciaire se régleront devant le tribunal compétent le plus proche de la salle CrossFit NANCY sans que l’autre partie ne puisse demander une quelconque indemnité de déplacement ou autre.",
	
"v	. Résiliation : L’adhérent ayant souscrit au contrat peut demander la résiliation de son contrat d’abonnement, en cas        de non utilisation définitive des prestations de la salle CrossFit NANCY par l’envoi d’une lettre recommandée avec accusé de réception, adressée au co-contractant. Cette résiliation ne sera effective qu’à l’issue d’un délai de 30 jours de préavis à compter de la date de réception de la demande par KOOPER inc - CROSSFIT NANCY Affiliate. dans ce cas, les montants forfaitaires    des cartes d’abonnement et du mois de préavis ne pourront faire l’objet d’aucun remboursement quel que soit le motif de la résiliation.",
"vi	. Conditions d’accès à la salle CrossFit NANCY : L’accès de l’adhérent à la salle ne sera autorisé qu’après que ce  dernier ait « badgé » sa carte d’adhérent à l’entrée, selon les horaires indiqués à la salle. Des horaires et un planning de cours allégés pourront être prévus du 1et juillet au 31 août de chaque année, une fermeture technique en août pourra être instaurée, ainsi que des horaires allégés les jours fériés : l’équipe de CROSSFIT NANCY préviendra alors ses adhérents via communication formelle (affichage d’une note interne et e-mailing). L’accès de toute personne non inscrite à la salle est formellement interdit, exception faite d’une visite accompagnée par un membre du personnel.",
"vii	. Etat de santé / dopage : L’adhérent atteste que sa condition physique et son état de santé lui permettent de pratiquer les activités de la salle. Il s’engage à remettre au plus tard dans les 8 jours qui suivent son inscription, un certificat médical d’aptitude daté de moins de 3 mois. Les conseillers sportifs de la salle CrossFit NANCY, à disposition de l’adhérent, et à la demande exclusive de ce dernier, pourront établir durant un entretien, un programme personnalisé et suivi, adapté à ses objectifs, ainsi qu’à sa constitution physique et médicale. En cas de modification de son état de santé, l’adhérent s’engage à fournir un certificat médical mis à jour à la salle CROSSFIT NANCY. L’adhérent s’engage à ne pas utiliser, ni commercialiser, toute substance chimique, organique ou assimilée, interdite ou recommandée par les services d’hygiène, la médecine sportive et les services de stupéfiants. Le non-respect de cette clause entraînera l’exclusion immédiate de la salle, sans préavis ni indemnité.",
"iX . Responsabilité : Conformément à la loi du 16/07/1984, relative à l’organisation et à la promotion des activités physiques et sportives, modifiée par la loi du au 13/07/1992, la salle CROSSFIT NANCY a souscrit une assurance de responsabilité civile et multirisque professionnelle pour son activité et celle de son personnel auprès de la compagnie dont les coordonnées sont affichées à la salle. La responsabilité de KOOPER inc - CROSSFIT NANCY Affiliate ne pourra être recherchée en cas d’accident résultant de l’inobservation des dispositions du présent contrat, des consignes mentionnées dans le règlement intérieur ci- après, ou d’une utilisation anormale des installations et appareils mis à disposition, ou d’un entraînement en autonomie dans le cadre des créneaux dits d’Open Box. L’adhérent qui contreviendrait, soit aux consignes d’utilisation des matériels, soit aux remarques faites par le personnel de la salle dans le cadre de ses activités, verra limité ou exclu son droit d’indemnisation en cas de dommage. La salle CROSSFIT NANCY ne saurait être tenue responsable de la violation délibérée de ses obligations par l’adhérent, et ne saurait en supporter les conséquences en vertu du droit commun de la responsabilité. Toute déclaration d’événement qui serait amenée à faire jouer les éventuelles garanties de la salle, devra faire l’objet d’une déclaration par   écrit en ARP au plus tard sous huit jours, sous peine de déchéance. de son côté, l’adhérent devra être titulaire d’une police d’assurance de personne, au titre de sa responsabilité civile, et renonce à couvrir ses activités au sein de la salle CROSSFIT NANCY par la souscription d’un contrat individuel accident auprès de l’assureur de KOOPER inc - CROSSFIT NANCY Affiliate,  en garantie complémentaire et à sa charge; le cas échéant il pourra en faire la demande expresse pat lettre recommandée directement auprès de KOOPER inc - CROSSFIT NANCY Affiliate.",

"X . Vestiaires, douches et WC : La salle met à disposition de l’adhérent des casiers individuels dans ses vestiaires. Leur utilisation est limitée à la durée de la séance d’entraînement. Il est rappelé expressément à l’adhérent que les casiers ne feront l’objet d’aucune surveillance spécifique; il reconnaît avoir été informé du risque encouru de laisser tout objet de valeur dans les vestiaires à usage collectif. L’adhérent fait son affaire personnelle quant au système de sécurité de son casier, notamment par le fait d’apporter son propre cadenas. La salle met également à disposition des douches et WC : l’adhérent s’engage à respecter les règles de savoir-vivre, d’hygiène et de sécurité.",

"Xi . Règlement intérieur : L’adhérent déclare avoir pris connaissance et accepté les consignes de comportement de sécurité du règlement intérieur, dont un exemplaire lui a été présenté à son inscription. Il reconnaît à la direction de la salle le droit d’exclure de l’établissement, par lettre motivée avec accusé de réception, toute personne dont l’attitude, le comportement voire la tenue seraient contraires aux bonnes mœurs, aux règles d’hygiène et de sécurité, ou notoirement gênant les autres adhérents, ou non-conformes au présent contrat ou au règlement intérieur.",
"Xii . Loi informatique & libertés : Le traitement informatique du dossier de l’adhérent dans le cadre de la loi « informatique et libertés » du 16/01/1978, lui ouvre un droit de rectification et d’opposition aux données du dossier. Sauf avis contraire de sa part, ces données pourront être exploitées par la salle ou l’un de ses partenaires, afin d’informer d’éventuelles autres prestations de KOOPER inc - CROSSFIT NANCY Affiliate.",
"Xiii . Lois applicables - réclamations : Les présentes conditions générales sont soumises à l’application du droit français en vigueur. Toute réclamation ou contestation doit être transmise par écrit à KOOPER Inc - 24/26 Boulevard du 26ème RI 54000 NANCY, ou à l’adresse suivante : contact@crossfit-nancy.fr. Les différends qui viendraient à se produire à propos de la validité, de l’interprétation, de l’exécution ou de l’inexécution des présentes conditions seront soumis à la médiation. L’abonné est informé de la possibilité qui lui est offerte par l’article L 152-1 du Code de la consommation, en cas de litige résultant des présentes conditions générales, d’avoir recours au médiateur national de la consommation. L’adhérent est informé que la saisine du médiateur ne peut intervenir qu’après avoir tenté au préalable de résoudre le litige directement auprès de KOOPER Inc par une réclamation écrite. En cas d’échec de la procédure de médiation, les différends qui viendraient à se produire à propos de la validité, de l’interprétation, de l’exécution ou de l’inexécution des présentes CGU seront soumis, par la partie la plus diligente, à la compétence exclusive des tribunaux du ressort de Nancy, nonobstant pluralité de défendeurs ou appel en garantie.");

        legalText.signatureInformationText = Arrays.asList("Qu'il a pris connaissance des conditions générales de vente exposées au verso du présent contrat d’adhésion", 
        								"Qu'un exemplaire du règlement intérieur lui a été présenté",
        								"Que les documents fournis par ses soins dans le cadre de cette adhésion sont valides et conformes pour faire valoir ce que de droit : copie d'une pièce d’identité non expirée, certificat médical de moins de 3 mois autorisant la pratique du CrossFit");
        Subscription sub = new Subscription();
        sub.setId(2157521L);
        sub.setMember(new Member());
        sub.getMember().setTitle(Title.MS);
        sub.getMember().setLastName("Gangloff");
        sub.setMembership(new Membership());
        sub.getMembership().setName("Triplet");
        sub.getMembership().setInformation("This is information");
        sub.getMembership().setPriceTaxIncl(75.0);
        sub.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        sub.setDirectDebit(new SubscriptionDirectDebit());
        sub.getDirectDebit().setFirstPaymentTaxIncl(33.5);
        sub.getDirectDebit().setFirstPaymentMethod(PaymentMethod.BANK_CHECK);
        sub.getDirectDebit().setAtDayOfMonth(3);
        sub.getDirectDebit().setAfterDate(new LocalDate());
        sub.getDirectDebit().setAmount(55.0);
        sub.setSignatureDate(new DateTime());
        sub.setSignatureDataEncoded("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAMAAAC6V+0/AAAAwFBMVEXm7NK41k3w8fDv7+q01Tyy0zqv0DeqyjOszDWnxjClxC6iwCu11z6y1DvA2WbY4rCAmSXO3JZDTxOiwC3q7tyryzTs7uSqyi6tzTCmxSukwi9aaxkWGga+3FLv8Ozh6MTT36MrMwywyVBziSC01TbT5ZW9z3Xi6Mq2y2Xu8Oioxy7f572qxzvI33Tb6KvR35ilwTmvykiwzzvV36/G2IPw8O++02+btyepyDKvzzifvSmw0TmtzTbw8PAAAADx8fEC59dUAAAA50lEQVQYV13RaXPCIBAG4FiVqlhyX5o23vfVqUq6mvD//1XZJY5T9xPzzLuwgKXKslQvZSG+6UXgCnFePtBE7e/ivXP/nRvUUl7UqNclvO3rpLqofPDAD8xiu2pOntjamqRy/RqZxs81oeVzwpCwfyA8A+8mLKFku9XfI0YnSKXnSYZ7ahSII+AwrqoMmEFKriAeVrqGM4O4Z+ADZIhjg3R6LtMpWuW0ERs5zunKVHdnnnMLNQqaUS0kyKkjE1aE98b8y9x9JYHH8aZXFMKO6JFMEvhucj3Wj0kY2D92HlHbE/9Vk77mD6srRZqmVEAZAAAAAElFTkSuQmCC");

        
        
        File tempFile = File.createTempFile("subscription", ".pdf");
        try(FileOutputStream os = new FileOutputStream(tempFile)){
            PdfSubscription.getBuilder().createPdf(legalText, sub, os);
        }
  
        System.out.println(tempFile.getPath());
        Desktop.getDesktop().open(tempFile);

    }
}
