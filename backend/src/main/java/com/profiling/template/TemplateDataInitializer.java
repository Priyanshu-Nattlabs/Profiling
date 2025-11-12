package com.profiling.template;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TemplateDataInitializer implements ApplicationRunner {

    private final TemplateRepository templateRepository;

    public TemplateDataInitializer(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Instant now = Instant.now();
        List<TemplateEntity> defaults = List.of(
                new TemplateEntity("professional", "Professional", "Extremely professional with fluent English",
                        "\uD83D\uDCBC", professionalTemplate(), "", now, now),
                new TemplateEntity("bio", "Bio", "Casual and friendly bio style", "\u2728", bioTemplate(), "", now,
                        now),
                new TemplateEntity("story", "Story", "Simple story-like narrative", "\uD83D\uDCD6", storyTemplate(),
                        "", now, now),
                new TemplateEntity("cover", "Cover Letter",
                        "Generate a tailored cover letter after providing company details", "\u2709\uFE0F",
                        coverLetterTemplate(), "", now, now),
                new TemplateEntity("modern-professional", "Modern Professional",
                        "Ambitious tone with modern professional presence", "\uD83E\uDDD1\u200D\uD83D\uDCBC",
                        modernProfessionalTemplate(), "", now, now),
                new TemplateEntity("industry", "Industry Ready",
                        "Balanced, employer-facing tone with practical highlights", "\uD83C\uDFED",
                        industryTemplate(), "", now, now),
                new TemplateEntity("executive", "Executive Professional Template",
                        "A confident and achievement-oriented profile template highlighting education, skills, and professional goals.", "\uD83C\uDFC6",
                        executiveTemplate(), "", now, now));

        defaults.stream()
                .filter(template -> !templateRepository.existsById(template.getId()))
                .forEach(templateRepository::save);
    }

    private String professionalTemplate() {
        return """
                I am {{name}}, a dedicated and accomplished student currently pursuing a {{currentDegree}} degree with a specialization in {{branch}} at {{institute}}. Presently in my {{yearOfStudy}} year of academic tenure, I have demonstrated exceptional commitment to professional development through the successful completion of distinguished certifications including {{certifications}}. Throughout my educational journey, I have achieved notable recognition for {{achievements}}, which underscores my unwavering dedication to excellence. My technical proficiencies encompass {{technicalSkills}}, complemented by refined soft skills such as {{softSkills}}, which collectively position me as a well-rounded professional.{{professionalInternshipSentence}}{{professionalExperienceSentence}} For professional correspondence, I can be reached at {{email}}, and I invite you to explore my comprehensive professional profile at {{linkedin}}. Date of Birth: {{dob}}.
                """;
    }

    private String bioTemplate() {
        return """
                Hey there! I'm {{name}} 👋 I'm a {{yearOfStudy}} year student studying {{branch}} in {{currentDegree}} at {{institute}}. I've picked up some cool certifications along the way like {{certifications}}, and I'm pretty proud of {{achievements}}! I love working with {{technicalSkills}}, and people tell me I'm good at {{softSkills}}. Want to connect? Drop me a line at {{email}} or check out my LinkedIn: {{linkedin}}.{{internshipClause}}{{experienceClause}} Let's chat!
                """;
    }

    private String storyTemplate() {
        return """
                This is the story of {{name}}, born on {{dob}}. From a young age, there was always a passion for learning and growth. Today, that journey has led to pursuing a {{currentDegree}} degree in {{branch}} at {{institute}}, now in the {{yearOfStudy}} year. Along this path, several milestones were reached - certifications earned in {{certifications}}, and memorable achievements like {{achievements}}. The skills developed include {{technicalSkills}}, paired with personal strengths in {{softSkills}}.{{internshipNarrative}}{{experienceNarrative}} This journey continues to unfold, and new chapters are being written every day. To be part of this story, reach out at {{email}} or connect through {{linkedin}}.
                """;
    }

    private String coverLetterTemplate() {
        return """
                {{companyName}}
                {{companyAddress}}

                Dear {{hiringManagerName}},

                I am writing to express my enthusiasm for the {{positionTitle}} role at {{companyName}}. With {{relevantExperience}}, I am eager to bring my dedication and passion to your team.

                During my journey, I have accomplished {{keyAchievement}}, demonstrating my ability to deliver meaningful results.{{internshipHighlight}}{{professionalHighlight}} I pride myself on strengths such as {{strengths}}, which I believe would be valuable at {{companyName}}.

                {{closingNote}} {{contactLine}}

                Best regards,
                {{name}}
                {{email}}
                {{signatureLinkedin}}
                """;
    }

    private String industryTemplate() {
        return """
                {{name}} is a dedicated and growth-oriented student currently pursuing a {{currentDegree}} degree in {{branch}} at {{institute}}.

                With academic experience spanning {{yearOfStudy}} year(s), {{name}} has continuously built a strong foundation in modern technical concepts and practical skills.

                Key technical strengths include {{technicalSkills}}, supported by essential soft skills such as {{softSkills}}, which enable effective teamwork, adaptability, and problem-solving in diverse environments.

                {{name}} has earned certifications such as {{certifications}}, demonstrating a commitment to continuous learning and professional development. Notable achievements include {{achievements}}, reflecting consistent effort and excellence.

                {{#hasInternship}}
                Practical exposure through internships like {{internshipDetails}} has strengthened industry readiness and improved hands-on competence.
                {{/hasInternship}}

                {{#hasExperience}}
                Additionally, meaningful experience in {{experienceDetails}} has contributed to a broader understanding of real-world challenges and project workflows.
                {{/hasExperience}}

                For communication or professional opportunities, {{name}} can be reached at {{email}}.
                LinkedIn: {{linkedin}}
                Date of Birth: {{dob}}
                """;
    }

    private String modernProfessionalTemplate() {
        return """
                {{name}} is an ambitious and dedicated student currently pursuing a {{currentDegree}} degree in {{branch}} at {{institute}}. With a strong academic foundation and {{yearOfStudy}} year(s) of experience in structured learning, {{name}} consistently demonstrates curiosity, discipline, and an eagerness to master new concepts.

                A key strength of {{name}} lies in technical competency across {{technicalSkills}}, supported by essential soft skills such as {{softSkills}}. This balanced skill set enables effective collaboration, problem-solving, communication, and adaptability in both academic and project-driven environments.

                {{name}} has earned certifications including {{certifications}}, showcasing a commitment to continuous learning and professional growth. Additionally, achievements such as {{achievements}} highlight {{name}}'s ability to deliver results, take initiative, and excel in competitive or challenging situations.

                {{#hasInternship}}
                Real-world exposure through internships like {{internshipDetails}} has further empowered {{name}} with hands-on experience, practical understanding of industry expectations, and enhanced confidence in applying theoretical knowledge.
                {{/hasInternship}}

                {{#hasExperience}}
                Beyond academics, {{name}} has gained meaningful experience in {{experienceDetails}}, contributing to broader insights into professional workflows, project execution, and team communication.
                {{/hasExperience}}

                Driven, focused, and ready to embrace new opportunities, {{name}} aims to apply these skills in impactful real-world roles while continuing to grow personally and professionally.

                For communication or collaborations, {{name}} can be reached at {{email}}.
                LinkedIn: {{linkedin}}
                Date of Birth: {{dob}}
                """;
    }

    private String executiveTemplate() {
        return """
                My name is {{name}}, a results-driven and ambitious student currently pursuing a {{currentDegree}} in {{branch}} at {{institute}}. As a {{yearOfStudy}} year student, I have consistently demonstrated excellence in academics and practical learning. I hold certifications in {{certifications}}, which have strengthened my technical foundation and problem-solving capabilities. Recognized for {{achievements}}, I continue to seek opportunities to expand my expertise and make meaningful contributions. My core technical skills include {{technicalSkills}}, complemented by strong interpersonal and organizational abilities such as {{softSkills}}.{{internshipDetails}}{{experienceDetails}} I am eager to collaborate in dynamic environments where innovation meets execution. For any professional inquiries, please contact me at {{email}} or connect with me on LinkedIn: {{linkedin}}. Date of Birth: {{dob}}.
                """;
    }
}

