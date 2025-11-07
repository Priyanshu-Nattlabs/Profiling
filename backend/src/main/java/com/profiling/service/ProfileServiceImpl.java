package com.profiling.service;

import com.profiling.model.Profile;
import com.profiling.model.ProfileResponse;
import com.profiling.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public ProfileResponse saveProfile(Profile profile) {
        // TODO: Add business logic for profile validation or processing before saving
        // TODO: Add logging for profile creation
        
        // Save profile to MongoDB
        Profile savedProfile = profileRepository.save(profile);
        
        // Generate template paragraph
        String template = generateProfileTemplate(savedProfile);
        
        // Return response with profile and template
        return new ProfileResponse(savedProfile, template);
    }
    
    /**
     * Generate a profile template paragraph from the profile data
     * @param p The profile object
     * @return A formatted paragraph describing the profile
     */
    private String generateProfileTemplate(Profile p) {
        String templateType = valueOrDefault(p.getTemplateType(), "professional");
        
        switch (templateType.toLowerCase()) {
            case "bio":
                return generateBioTemplate(p);
            case "story":
                return generateStoryTemplate(p);
            case "cover":
                return generateCoverLetterTemplate(p);
            case "professional":
            default:
                return generateProfessionalTemplate(p);
        }
    }
    
    /**
     * Generate an extremely professional template with fluent English
     */
    private String generateProfessionalTemplate(Profile p) {
        String name = valueOrDefault(p.getName(), "[name]");
        String currentDegree = valueOrDefault(p.getCurrentDegree(), "[degree]");
        String branch = valueOrDefault(p.getBranch(), "[branch]");
        String institute = valueOrDefault(p.getInstitute(), "[institute]");
        String yearOfStudy = valueOrDefault(p.getYearOfStudy(), "[year]");
        String certifications = valueOrDefault(p.getCertifications(), "[certifications]");
        String achievements = valueOrDefault(p.getAchievements(), "[achievements]");
        String technicalSkills = valueOrDefault(p.getTechnicalSkills(), "[technical skills]");
        String softSkills = valueOrDefault(p.getSoftSkills(), "[soft skills]");
        String email = valueOrDefault(p.getEmail(), "[email]");
        String linkedin = valueOrDefault(p.getLinkedin(), "[LinkedIn profile]");
        String dob = valueOrDefault(p.getDob(), "[date of birth]");
        
        return String.format(
            "I am %s, a dedicated and accomplished student currently pursuing a %s degree with a specialization in %s at %s. " +
            "Presently in my %s year of academic tenure, I have demonstrated exceptional commitment to professional development " +
            "through the successful completion of distinguished certifications including %s. Throughout my educational journey, " +
            "I have achieved notable recognition for %s, which underscores my unwavering dedication to excellence. " +
            "My technical proficiencies encompass %s, complemented by refined soft skills such as %s, " +
            "which collectively position me as a well-rounded professional. For professional correspondence, " +
            "I can be reached at %s, and I invite you to explore my comprehensive professional profile at %s. " +
            "Date of Birth: %s.",
            name, currentDegree, branch, institute, yearOfStudy, certifications, 
            achievements, technicalSkills, softSkills, email, linkedin, dob
        );
    }
    
    /**
     * Generate a casual bio-style template
     */
    private String generateBioTemplate(Profile p) {
        String name = valueOrDefault(p.getName(), "[name]");
        String currentDegree = valueOrDefault(p.getCurrentDegree(), "[degree]");
        String branch = valueOrDefault(p.getBranch(), "[branch]");
        String institute = valueOrDefault(p.getInstitute(), "[institute]");
        String yearOfStudy = valueOrDefault(p.getYearOfStudy(), "[year]");
        String certifications = valueOrDefault(p.getCertifications(), "[certifications]");
        String achievements = valueOrDefault(p.getAchievements(), "[achievements]");
        String technicalSkills = valueOrDefault(p.getTechnicalSkills(), "[technical skills]");
        String softSkills = valueOrDefault(p.getSoftSkills(), "[soft skills]");
        String email = valueOrDefault(p.getEmail(), "[email]");
        String linkedin = valueOrDefault(p.getLinkedin(), "[LinkedIn profile]");
        
        return String.format(
            "Hey there! I'm %s 👋 I'm a %s year student studying %s in %s at %s. " +
            "I've picked up some cool certifications along the way like %s, and I'm pretty proud of %s! " +
            "I love working with %s, and people tell me I'm good at %s. " +
            "Want to connect? Drop me a line at %s or check out my LinkedIn: %s. Let's chat!",
            name, yearOfStudy, branch, currentDegree, institute, certifications, 
            achievements, technicalSkills, softSkills, email, linkedin
        );
    }
    
    /**
     * Generate a simple story-like template
     */
    private String generateStoryTemplate(Profile p) {
        String name = valueOrDefault(p.getName(), "[name]");
        String currentDegree = valueOrDefault(p.getCurrentDegree(), "[degree]");
        String branch = valueOrDefault(p.getBranch(), "[branch]");
        String institute = valueOrDefault(p.getInstitute(), "[institute]");
        String yearOfStudy = valueOrDefault(p.getYearOfStudy(), "[year]");
        String certifications = valueOrDefault(p.getCertifications(), "[certifications]");
        String achievements = valueOrDefault(p.getAchievements(), "[achievements]");
        String technicalSkills = valueOrDefault(p.getTechnicalSkills(), "[technical skills]");
        String softSkills = valueOrDefault(p.getSoftSkills(), "[soft skills]");
        String email = valueOrDefault(p.getEmail(), "[email]");
        String linkedin = valueOrDefault(p.getLinkedin(), "[LinkedIn profile]");
        String dob = valueOrDefault(p.getDob(), "[date of birth]");
        
        return String.format(
            "This is the story of %s, born on %s. From a young age, there was always a passion for learning and growth. " +
            "Today, that journey has led to pursuing a %s degree in %s at %s, now in the %s year. " +
            "Along this path, several milestones were reached - certifications earned in %s, and memorable achievements like %s. " +
            "The skills developed include %s, paired with personal strengths in %s. " +
            "This journey continues to unfold, and new chapters are being written every day. " +
            "To be part of this story, reach out at %s or connect through %s.",
            name, dob, currentDegree, branch, institute, yearOfStudy, certifications, 
            achievements, technicalSkills, softSkills, email, linkedin
        );
    }

    /**
     * Generate a detailed cover letter template
     */
    private String generateCoverLetterTemplate(Profile p) {
        String applicantName = valueOrDefault(p.getName(), "[Your Name]");
        String email = valueOrDefault(p.getEmail(), "[your.email@example.com]");
        String linkedin = valueOrDefault(p.getLinkedin(), "");
        String managerName = valueOrDefault(p.getHiringManagerName(), "Hiring Manager");
        String companyName = valueOrDefault(p.getCompanyName(), "[Company Name]");
        String companyAddress = valueOrDefault(p.getCompanyAddress(), "[Company Address]");
        String positionTitle = valueOrDefault(p.getPositionTitle(), "[Position Title]");
        String experience = valueOrDefault(p.getRelevantExperience(),
                "my background in " + valueOrDefault(p.getCurrentDegree(), "[your degree]") + " and " + valueOrDefault(p.getBranch(), "[specialisation]"));
        String achievement = valueOrDefault(p.getKeyAchievement(), valueOrDefault(p.getAchievements(), "a recent accomplishment"));
        String strengths = valueOrDefault(p.getStrengths(), valueOrDefault(p.getSoftSkills(), "collaboration and adaptability"));
        String closingNote = valueOrDefault(p.getClosingNote(),
                "Thank you for taking the time to review my application. I would welcome the opportunity to discuss how I can contribute to " + companyName + ".");

        String header = String.format("%s\n%s", companyName, companyAddress);
        String greeting = String.format("Dear %s,", managerName);

        String paragraphOne = String.format(
                "I am writing to express my enthusiasm for the %s role at %s. With %s, I am eager to bring my dedication and passion to your team.",
                positionTitle, companyName, experience);

        String paragraphTwo = String.format(
                "During my journey, I have accomplished %s, demonstrating my ability to deliver meaningful results. I pride myself on strengths such as %s, which I believe would be valuable at %s.",
                achievement, strengths, companyName);

        String contactLine;
        if (linkedin != null && !linkedin.isBlank()) {
            contactLine = String.format("You can reach me at %s or connect via LinkedIn: %s.", email, linkedin);
        } else {
            contactLine = String.format("You can reach me at %s.", email);
        }

        String closingParagraph = String.format("%s %s", closingNote, contactLine);

        String signature = String.format("Best regards,\n%s\n%s", applicantName, email);
        if (linkedin != null && !linkedin.isBlank()) {
            signature = signature + String.format("\n%s", linkedin);
        }

        return String.join("\n\n", header, greeting, paragraphOne, paragraphTwo, closingParagraph, signature);
    }

    private String valueOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    @Override
    public Optional<Profile> getProfileById(String id) {
        // TODO: Add business logic for profile retrieval (e.g., access control, caching)
        // TODO: Add logging for profile retrieval
        return profileRepository.findById(id);
    }
}

