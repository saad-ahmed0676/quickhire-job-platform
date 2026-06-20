package com.quickhire.ui;

import com.quickhire.dao.ApplicationDAO;
import com.quickhire.dao.DisputeDAO;
import com.quickhire.dao.ReviewDAO;
import com.quickhire.dao.UserDAO;
import com.quickhire.model.*;
import com.quickhire.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MasterController {

    public static MicroJob selectedJob = null;
    public static MicroJob activeJob = null;
    public static Invoice activeInvoice = null;
    public static Dispute activeDispute = null;

    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();
    private final ApplicationService appService = new ApplicationService();
    private final InvoiceService invoiceService = new InvoiceService();
    private final PaymentService paymentService = new PaymentService();
    private final DashboardService dashboardService = new DashboardService();
    private final ReviewService reviewService = new ReviewService();

    @FXML private VBox sidebarContainer;
    @FXML private Label errorLabel;
    @FXML private Label statusLabel;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private TextField nameField;
    @FXML private RadioButton seekerRadio;
    @FXML private RadioButton providerRadio;
    private final ToggleGroup roleGroup = new ToggleGroup();

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private TableView<MicroJob> openJobsTable;
    @FXML private TableColumn<MicroJob, String> colOpenTitle, colOpenCategory, colOpenLocation, colOpenStatus;
    @FXML private TableColumn<MicroJob, Double> colOpenRate;
    @FXML private TableView<MicroJob> inProgressTable;
    @FXML private TableColumn<MicroJob, String> colProgTitle, colProgCategory, colProgLocation, colProgStatus;
    @FXML private TableColumn<MicroJob, Double> colProgRate;
    @FXML private TableView<MicroJob> completedTable;
    @FXML private TableColumn<MicroJob, String> colDoneTitle, colDoneCategory, colDoneLocation, colDoneStatus;
    @FXML private TableColumn<MicroJob, Double> colDoneRate;

    @FXML private ComboBox<String> categoryFilter;
    @FXML private TextField locationFilter;
    @FXML private TextField minRateField;
    @FXML private TableView<MicroJob> jobsTable;
    @FXML private TableColumn<MicroJob, String> titleCol, categoryCol, locationCol, providerCol, statusCol;
    @FXML private TableColumn<MicroJob, Double> rateCol;

    @FXML private Label jobTitleLabel;
    @FXML private Label jobCategoryLabel;
    @FXML private Label jobLocationLabel;
    @FXML private Label jobRateLabel;
    @FXML private Label jobProviderLabel;
    @FXML private TextArea jobDescArea;
    @FXML private TextArea noteArea;
    private MicroJob currentJob;

    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private TextField hourlyRateField;
    @FXML private TextArea descriptionArea;

    @FXML private ComboBox<MicroJob> jobCombo;
    @FXML private TableView<JobApplication> applicantsTable;
    @FXML private TableColumn<JobApplication, String> nameCol;
    @FXML private TableColumn ratingCol;
    @FXML private TableColumn<JobApplication, String> noteCol;
    @FXML private TableColumn dateCol;

    @FXML private ListView<String> updatesListView;

    @FXML private VBox entryPanel;
    @FXML private VBox jobInfoBox;
    @FXML private TextField hoursField;
    @FXML private TextField supplyCostField;
    @FXML private Label entryErrorLabel;
    @FXML private VBox previewPanel;
    @FXML private Label previewJobTitle;
    @FXML private Label previewHours;
    @FXML private Label previewRate;
    @FXML private Label previewLabour;
    @FXML private Label previewSupply;
    @FXML private Label previewTotal;
    @FXML private Label previewErrorLabel;
    private Invoice generatedInvoice;

    @FXML private VBox invoiceDetailsBox;
    @FXML private Label detailJobTitle;
    @FXML private Label detailHours;
    @FXML private Label detailRate;
    @FXML private Label detailSupply;
    @FXML private Label detailTotal;
    @FXML private Button approveBtn;
    @FXML private Button disputeBtn;
    @FXML private VBox challengePanel;
    @FXML private Label challengeReasonLabel;
    @FXML private Label challengeAmountLabel;
    @FXML private Button acceptProposedBtn;
    @FXML private Button reDisputeBtn;
    private Invoice currentInvoice;
    private Dispute currentDispute;

    @FXML private VBox reviewPanel;
    @FXML private Label counterpartyLabel;
    @FXML private RadioButton star1, star2, star3, star4, star5;
    @FXML private TextArea commentArea;
    @FXML private Label alreadyReviewedLabel;
    @FXML private Label reviewErrorLabel;
    @FXML private Button submitBtn;
    private int counterpartyUserId = -1;
    private ToggleGroup ratingGroup;

    @FXML private Button editBtn;
    @FXML private Button cancelBtn;
    @FXML private VBox editForm;
    @FXML private TextField editTitle;
    @FXML private ComboBox<String> editCategory;
    @FXML private TextField editLocation;
    @FXML private TextField editRate;
    @FXML private TextArea editDescription;
    @FXML private Label editErrorLabel;
    private MicroJob jobBeingEdited;

    @FXML private ListView<NotificationStore.Notification> notifList;
    @FXML private Label unreadCountLabel;
    private MasterController embeddedSidebarCtrl;

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label ratingLabel;
    @FXML private TextArea bioArea;
    @FXML private TextField contactField;
    @FXML private Label skillsHeaderLabel;
    @FXML private TextField skillsField;

    @FXML private TableView<Review> reviewsTable;
    @FXML private Label overallRatingLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private TableColumn<Review, String> reviewerCol;
    @FXML private TableColumn starsCol;
    @FXML private TableColumn<Review, String> commentCol;

    @FXML private Label disputeReasonLabel;
    @FXML private Label invoiceTotalLabel;
    @FXML private Label roundLabel;
    @FXML private TextArea challengeArea;
    @FXML private TextField proposedAmountField;

    @FXML private Button btnDashboard;
    @FXML private Button btnNotifications;
    @FXML private Button btnFindJobs;
    @FXML private Button btnUpdateProgress;
    @FXML private Button btnCompleteJob;
    @FXML private Button btnPostJob;
    @FXML private Button btnReviewApplicants;
    @FXML private Button btnManagePostings;
    @FXML private Button btnReviewInvoice;
    @FXML private Button btnRateReview;
    @FXML private Button btnProfile;
    @FXML private Button btnLogout;
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label notifBadge;
    @FXML private Label applicantBadge;
    @FXML private VBox seekerNav;
    @FXML private VBox providerNav;
    @FXML private Label avatarLabel;

    @FXML
    public void initialize() {
        if (emailField != null && seekerRadio == null) {
            initLogin();
        } else if (seekerRadio != null) {
            initRegister();
        } else if (welcomeLabel != null) {
            initDashboard();
        } else if (categoryFilter != null) {
            initFindJobs();
        } else if (jobDescArea != null) {
            initApplyJob();
        } else if (categoryCombo != null) {
            initPostJob();
        } else if (applicantsTable != null) {
            initReviewApplicants();
        } else if (updatesListView != null) {
            initUpdateProgress();
        } else if (entryPanel != null) {
            initCompleteJob();
        } else if (approveBtn != null) {
            initReviewInvoice();
        } else if (star1 != null) {
            initRateReview();
        } else if (editForm != null) {
            initManagePostings();
        } else if (notifList != null) {
            initNotifications();
        } else if (bioArea != null) {
            initProfile();
        } else if (reviewsTable != null) {
            initMyReviews();
        } else if (challengeArea != null) {
            initChallengeDispute();
        }
    }

    private MasterController loadSidebar(String activeButton) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/quickhire/fxml/sidebar.fxml"));
            VBox sidebar = loader.load();
            MasterController ctrl = loader.getController();
            ctrl.initUser(SessionManager.getCurrentUser());
            ctrl.setActiveButton(activeButton);
            sidebarContainer.getChildren().add(sidebar);
            return ctrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initLogin() {
        errorLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        errorLabel.setText("");
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            return;
        }
        try {
            User user = userService.loginUser(email, password);
            SessionManager.setCurrentUser(user);
            MainApp.switchScene("dashboard.fxml");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        MainApp.switchScene("register.fxml");
    }

    private void initRegister() {
        seekerRadio.setToggleGroup(roleGroup);
        providerRadio.setToggleGroup(roleGroup);
        seekerRadio.setSelected(true);
    }

    @FXML
    private void handleRegister() {
        errorLabel.setText("");
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        RadioButton selected = (RadioButton) roleGroup.getSelectedToggle();
        String role = (selected != null) ? (String) selected.getUserData() : "";
        try {
            User user = userService.registerUser(name, email, password, role);
            SessionManager.setCurrentUser(user);
            MainApp.switchScene("dashboard.fxml");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        MainApp.switchScene("login.fxml");
    }

    private void initDashboard() {
        loadSidebar("dashboard");
        setupDashboardColumns();
        loadDashboardData();
    }

    private void setupDashboardColumns() {
        colOpenTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colOpenCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colOpenLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colOpenRate.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        colOpenStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colProgTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colProgCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colProgLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colProgRate.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        colProgStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colDoneTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDoneCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDoneLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colDoneRate.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        colDoneStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadDashboardData() {
        User user = SessionManager.getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getName());
        roleLabel.setText(user.getRoleLabel());
        if (user.isProvider()) {
            roleLabel.getStyleClass().clear();
            roleLabel.getStyleClass().add("top-bar-role-badge-provider");
        }
        try {
            List<MicroJob> allJobs = dashboardService.getUserJobs(user);
            openJobsTable.setItems(FXCollections.observableArrayList(
                    allJobs.stream()
                            .filter(j -> MicroJob.STATUS_OPEN.equals(j.getStatus())
                                    || MicroJob.STATUS_PENDING_PAYMENT.equals(j.getStatus()))
                            .collect(Collectors.toList())));
            inProgressTable.setItems(FXCollections.observableArrayList(
                    allJobs.stream()
                            .filter(j -> MicroJob.STATUS_IN_PROGRESS.equals(j.getStatus()))
                            .collect(Collectors.toList())));
            completedTable.setItems(FXCollections.observableArrayList(
                    allJobs.stream()
                            .filter(j -> MicroJob.STATUS_SETTLED.equals(j.getStatus())
                                    || MicroJob.STATUS_CANCELLED.equals(j.getStatus()))
                            .collect(Collectors.toList())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewOpenJob() {
        User user = SessionManager.getCurrentUser();
        if (user.isProvider()) {
            MainApp.switchScene("review_applicants.fxml");
        } else {
            MainApp.switchScene("find_jobs.fxml");
        }
    }

    @FXML
    private void handleViewProgressJob() {
        User user = SessionManager.getCurrentUser();
        if (user.isSeeker()) {
            MainApp.switchScene("update_progress.fxml");
        } else {
            MainApp.switchScene("review_invoice.fxml");
        }
    }

    private void initFindJobs() {
        loadSidebar("findJobs");
        setupFindJobsTable();
        categoryFilter.setItems(FXCollections.observableArrayList(
                "All", "Cleaning", "Cooking", "Laundry", "Delivery",
                "Gardening", "Plumbing", "Painting", "Other"));
        categoryFilter.setValue("All");
        loadAllJobs();
    }

    private void setupFindJobsTable() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        rateCol.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        providerCol.setCellValueFactory(new PropertyValueFactory<>("providerName"));
    }

    private void loadAllJobs() {
        try {
            List<MicroJob> jobs = jobService.getAllOpenJobs();
            jobsTable.setItems(FXCollections.observableArrayList(jobs));
            statusLabel.setText(jobs.size() + " job(s) available.");
        } catch (Exception e) {
            statusLabel.setText("Error loading jobs: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String category = categoryFilter.getValue();
        String location = locationFilter.getText().trim();
        String rateText = minRateField.getText().trim();
        String categoryArg = (category == null || category.equals("All")) ? "" : category;
        double minRate = 0;
        if (!rateText.isEmpty()) {
            try {
                minRate = Double.parseDouble(rateText);
            } catch (NumberFormatException e) {
                statusLabel.setText("Min rate must be a number.");
                return;
            }
        }
        try {
            List<MicroJob> jobs = jobService.filterJobs(categoryArg, location, minRate);
            jobsTable.setItems(FXCollections.observableArrayList(jobs));
            statusLabel.setText(jobs.size() + " job(s) found.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowAll() {
        categoryFilter.setValue("All");
        locationFilter.clear();
        minRateField.clear();
        loadAllJobs();
    }

    @FXML
    private void handleApply() {
        MicroJob selected = jobsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a job from the list first.");
            return;
        }
        selectedJob = selected;
        MainApp.switchScene("apply_job.fxml");
    }

    private void initApplyJob() {
        loadSidebar("findJobs");
        errorLabel.setText("");
        currentJob = selectedJob;
        if (currentJob != null) {
            jobTitleLabel.setText(currentJob.getTitle());
            jobCategoryLabel.setText("Category: " + currentJob.getCategory());
            jobLocationLabel.setText("Location: " + currentJob.getLocation());
            jobRateLabel.setText("Rate: PKR " + currentJob.getHourlyRate() + " / hr");
            jobProviderLabel.setText("Posted by: " + currentJob.getProviderName());
            jobDescArea.setText(currentJob.getDescription());
        } else {
            errorLabel.setText("No job selected. Please go back and select a job.");
        }
    }

    @FXML
    private void handleSubmit() {
        errorLabel.setText("");
        if (currentJob == null) {
            errorLabel.setText("No job selected. Please go back and select a job.");
            return;
        }
        User currentUser = SessionManager.getCurrentUser();
        String note = noteArea.getText().trim();
        try {
            appService.submitApplication(currentJob.getJobId(), currentUser.getUserId(), note);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Application Submitted");
            alert.setHeaderText(null);
            alert.setContentText("Your application has been submitted successfully!");
            alert.showAndWait();
            MainApp.switchScene("find_jobs.fxml");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleViewProviderReviews() {
        if (currentJob == null) {
            errorLabel.setText("No job selected.");
            return;
        }
        int providerId = currentJob.getProviderId();
        String providerName = currentJob.getProviderName();
        try {
            ReviewDAO reviewDAO = new ReviewDAO();
            List<Review> reviews = reviewDAO.getReviewsByRevieweeId(providerId);
            showReviewDialog("Provider Reviews", "Reviews for: " + providerName, providerName, reviews);
        } catch (Exception e) {
            errorLabel.setText("Could not load reviews: " + e.getMessage());
        }
    }

    private void initPostJob() {
        loadSidebar("postJob");
        errorLabel.setText("");
        categoryCombo.setItems(FXCollections.observableArrayList(
                "Cleaning", "Cooking", "Laundry", "Delivery",
                "Gardening", "Plumbing", "Painting", "Other"));
    }

    @FXML
    private void handleSubmitPostJob() {
        errorLabel.setText("");
        String title = titleField.getText().trim();
        String category = categoryCombo.getValue();
        String location = locationField.getText().trim();
        String rateText = hourlyRateField.getText().trim();
        String description = descriptionArea.getText().trim();
        if (title.isEmpty() || location.isEmpty() || rateText.isEmpty() || description.isEmpty()) {
            errorLabel.setText("All fields marked * are required.");
            return;
        }
        if (category == null) {
            errorLabel.setText("Please select a category.");
            return;
        }
        double hourlyRate;
        try {
            hourlyRate = Double.parseDouble(rateText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Hourly rate must be a valid number (e.g. 500).");
            return;
        }
        User currentUser = SessionManager.getCurrentUser();
        try {
            MicroJob job = jobService.postJob(
                    currentUser.getUserId(), title, category, description, location, hourlyRate);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Job \"" + job.getTitle() + "\" posted successfully!");
            alert.showAndWait();
            MainApp.switchScene("dashboard.fxml");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void initReviewApplicants() {
        loadSidebar("reviewApplicants");
        setupApplicantsTable();
        loadProviderOpenJobs();
        jobCombo.setOnAction(e -> loadApplicants());
    }

    private void setupApplicantsTable() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("seekerName"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("seekerRating"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("applicationNote"));
        dateCol.setCellFactory(col -> new TableCell<JobApplication, Object>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    JobApplication app = (JobApplication) getTableRow().getItem();
                    setText(app.getSubmissionDate() != null ? app.getSubmissionDate().format(fmt) : "");
                }
            }
        });
        jobCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
        jobCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
    }

    private void loadProviderOpenJobs() {
        User currentUser = SessionManager.getCurrentUser();
        try {
            List<MicroJob> jobs = jobService.getJobsByProvider(currentUser.getUserId());
            jobs.removeIf(j -> !MicroJob.STATUS_OPEN.equals(j.getStatus()));
            jobCombo.setItems(FXCollections.observableArrayList(jobs));
            statusLabel.setText("Select a job to see its applicants.");
        } catch (Exception e) {
            statusLabel.setText("Error loading your jobs: " + e.getMessage());
        }
    }

    private void loadApplicants() {
        MicroJob selected = jobCombo.getValue();
        if (selected == null) return;
        try {
            List<JobApplication> apps = appService.getApplicationsForJob(selected.getJobId());
            applicantsTable.setItems(FXCollections.observableArrayList(apps));
            statusLabel.setText(apps.size() + " applicant(s) for: " + selected.getTitle());
        } catch (Exception e) {
            statusLabel.setText("Error loading applicants: " + e.getMessage());
        }
    }

    @FXML
    private void handleHire() {
        MicroJob selectedJob = jobCombo.getValue();
        JobApplication selectedApp = applicantsTable.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            statusLabel.setText("Please select a job first.");
            return;
        }
        if (selectedApp == null) {
            statusLabel.setText("Please select an applicant to hire.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Hire");
        confirm.setHeaderText("Hire " + selectedApp.getSeekerName() + "?");
        confirm.setContentText(
                "This will accept this applicant and reject all others.\n" +
                        "The job status will change to IN PROGRESS.");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    appService.hireApplicant(selectedApp.getApplicationId(), selectedJob.getJobId());
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Hired!");
                    success.setHeaderText(null);
                    success.setContentText(selectedApp.getSeekerName() +
                            " has been hired. Job is now IN PROGRESS.");
                    success.showAndWait();
                    MainApp.switchScene("dashboard.fxml");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleViewSeekerReviews() {
        JobApplication selectedApp = applicantsTable.getSelectionModel().getSelectedItem();
        if (selectedApp == null) {
            statusLabel.setText("Please select an applicant first to view their reviews.");
            return;
        }
        int seekerId = selectedApp.getSeekerId();
        String seekerName = selectedApp.getSeekerName();
        try {
            ReviewDAO reviewDAO = new ReviewDAO();
            List<Review> reviews = reviewDAO.getReviewsByRevieweeId(seekerId);
            showReviewDialog("Seeker Reviews", "Reviews for: " + seekerName, seekerName, reviews);
        } catch (Exception e) {
            statusLabel.setText("Could not load reviews: " + e.getMessage());
        }
    }

    private void showReviewDialog(String title, String header, String personName, List<Review> reviews) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setPrefWidth(520);
        if (reviews.isEmpty()) {
            Label noReviews = new Label(personName + " has not received any reviews yet.");
            noReviews.setStyle("-fx-text-fill: #888; -fx-font-size: 13;");
            content.getChildren().add(noReviews);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            Label avgLabel = new Label(String.format("Overall Rating: %.2f / 5.00  (%d reviews)", avg, reviews.size()));
            avgLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: #f39c12;");
            content.getChildren().add(avgLabel);
            content.getChildren().add(new Separator());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            for (Review r : reviews) {
                VBox card = new VBox(4);
                card.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; " +
                        "-fx-background-radius: 6; -fx-border-color: #ddd; -fx-border-radius: 6;");
                String stars = "★".repeat(r.getRating()) + "☆".repeat(5 - r.getRating());
                Label ratingLine = new Label(stars + "  (" + r.getRating() + "/5)  — "
                        + (r.getReviewerName() != null ? r.getReviewerName() : "Anonymous"));
                ratingLine.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 13; -fx-font-weight: bold;");
                Label commentLine = new Label(
                        (r.getComment() != null && !r.getComment().isEmpty()) ? r.getComment() : "(No comment)");
                commentLine.setWrapText(true);
                commentLine.setStyle("-fx-text-fill: #333;");
                Label dateLine = new Label(r.getReviewDate() != null ? r.getReviewDate().format(fmt) : "");
                dateLine.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11;");
                card.getChildren().addAll(ratingLine, commentLine, dateLine);
                content.getChildren().add(card);
            }
        }
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(380);
        scrollPane.setStyle("-fx-background-color: transparent;");
        dialog.getDialogPane().setContent(scrollPane);
        dialog.showAndWait();
    }

    private void initUpdateProgress() {
        loadSidebar("updateProgress");
        setupUpdateProgressCombo();
        loadSeekerInProgressJobs();
        jobCombo.setOnAction(e -> loadProgressHistory());
    }

    private void setupUpdateProgressCombo() {
        jobCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
        jobCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
    }

    private void loadSeekerInProgressJobs() {
        User currentUser = SessionManager.getCurrentUser();
        try {
            List<MicroJob> jobs = jobService.getJobsBySeeker(currentUser.getUserId());
            jobs.removeIf(j -> !MicroJob.STATUS_IN_PROGRESS.equals(j.getStatus()));
            jobCombo.setItems(FXCollections.observableArrayList(jobs));
            if (jobs.isEmpty()) {
                statusLabel.setText("You have no jobs currently In Progress.");
            } else {
                statusLabel.setText("Select a job to view or add progress updates.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading jobs: " + e.getMessage());
        }
    }

    private void loadProgressHistory() {
        MicroJob selectedJob = jobCombo.getValue();
        if (selectedJob == null) return;
        try {
            List<ProgressUpdate> updates = dashboardService.getProgressUpdates(selectedJob.getJobId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            if (updates.isEmpty()) {
                updatesListView.setItems(FXCollections.observableArrayList("No updates yet for this job."));
            } else {
                List<String> displayItems = updates.stream()
                        .map(u -> (u.getTimestamp() != null ? u.getTimestamp().format(fmt) : "Unknown time")
                                + "  —  " + u.getNote())
                        .collect(Collectors.toList());
                updatesListView.setItems(FXCollections.observableArrayList(displayItems));
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading updates: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmitUpdate() {
        statusLabel.setText("");
        MicroJob selectedJob = jobCombo.getValue();
        if (selectedJob == null) {
            statusLabel.setText("Please select a job first.");
            return;
        }
        String note = noteArea.getText().trim();
        if (note.isEmpty()) {
            statusLabel.setText("Please enter a progress note.");
            return;
        }
        try {
            dashboardService.submitProgressUpdate(selectedJob.getJobId(), note);
            noteArea.clear();
            loadProgressHistory();
            statusLabel.setText("Progress update submitted successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void initCompleteJob() {
        loadSidebar("completeJob");
        setupCompleteJobCombo();
        loadSeekerJobsForCompletion();
        jobCombo.setOnAction(e -> showJobInfo());
    }

    private void setupCompleteJobCombo() {
        jobCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
        jobCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
    }

    private void loadSeekerJobsForCompletion() {
        User currentUser = SessionManager.getCurrentUser();
        try {
            List<MicroJob> allJobs = jobService.getJobsBySeeker(currentUser.getUserId());
            List<MicroJob> visible = new ArrayList<>();
            for (MicroJob job : allJobs) {
                if (MicroJob.STATUS_IN_PROGRESS.equals(job.getStatus())) {
                    visible.add(job);
                } else if (MicroJob.STATUS_PENDING_PAYMENT.equals(job.getStatus())) {
                    try {
                        Invoice inv = invoiceService.getInvoiceByJobId(job.getJobId());
                        if (inv != null) {
                            Dispute d = paymentService.getDisputeByInvoiceId(inv.getInvoiceId());
                            if (d != null && d.isAwaitingSeekerResponse()) {
                                visible.add(job);
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }
            jobCombo.setItems(FXCollections.observableArrayList(visible));
            if (visible.isEmpty()) {
                entryErrorLabel.setText("You have no jobs currently In Progress.");
            } else {
                entryErrorLabel.setText("");
            }
        } catch (Exception e) {
            entryErrorLabel.setText("Error loading jobs: " + e.getMessage());
        }
    }

    private void showJobInfo() {
        MicroJob selected = jobCombo.getValue();
        if (selected == null) {
            jobInfoBox.setVisible(false);
            jobInfoBox.setManaged(false);
            return;
        }
        jobTitleLabel.setText(selected.getTitle());
        jobRateLabel.setText("Hourly Rate: PKR " + selected.getHourlyRate() + " / hr");
        jobLocationLabel.setText("Location: " + selected.getLocation());
        jobInfoBox.setVisible(true);
        jobInfoBox.setManaged(true);
        entryErrorLabel.setText("");
    }

    @FXML
    private void handleGenerateInvoice() {
        entryErrorLabel.setText("");
        MicroJob selectedJob = jobCombo.getValue();
        if (selectedJob == null) {
            entryErrorLabel.setText("Please select a job first.");
            return;
        }
        if (MicroJob.STATUS_PENDING_PAYMENT.equals(selectedJob.getStatus())) {
            entryErrorLabel.setText("This job has a dispute pending. Use '⚖️ Challenge Dispute' to respond.");
            return;
        }
        String hoursText = hoursField.getText().trim();
        String supplyText = supplyCostField.getText().trim();
        if (hoursText.isEmpty() || supplyText.isEmpty()) {
            entryErrorLabel.setText("Please fill in both Hours Worked and Supply Cost.");
            return;
        }
        double hours, supply;
        try {
            hours = Double.parseDouble(hoursText);
        } catch (NumberFormatException e) {
            entryErrorLabel.setText("Hours Worked must be a valid number (e.g. 3.5).");
            return;
        }
        try {
            supply = Double.parseDouble(supplyText);
        } catch (NumberFormatException e) {
            entryErrorLabel.setText("Supply Cost must be a valid number (enter 0 if none).");
            return;
        }
        try {
            generatedInvoice = invoiceService.generateInvoice(selectedJob.getJobId(), hours, supply);
            double labourCost = hours * selectedJob.getHourlyRate();
            previewJobTitle.setText(selectedJob.getTitle());
            previewHours.setText(hours + " hrs");
            previewRate.setText("PKR " + selectedJob.getHourlyRate() + " / hr");
            previewLabour.setText("PKR " + String.format("%.2f", labourCost));
            previewSupply.setText("PKR " + String.format("%.2f", supply));
            previewTotal.setText("PKR " + String.format("%.2f", generatedInvoice.getTotalAmount()));
            previewErrorLabel.setText("");
            entryPanel.setVisible(false);
            entryPanel.setManaged(false);
            previewPanel.setVisible(true);
            previewPanel.setManaged(true);
        } catch (Exception e) {
            entryErrorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleConfirmInvoice() {
        previewErrorLabel.setText("");
        if (generatedInvoice == null) {
            previewErrorLabel.setText("No invoice to confirm.");
            return;
        }
        MicroJob selectedJob = jobCombo.getValue();
        if (selectedJob == null) return;
        try {
            invoiceService.confirmInvoice(generatedInvoice.getInvoiceId(), selectedJob.getJobId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invoice Submitted");
            alert.setHeaderText(null);
            alert.setContentText("Invoice confirmed and submitted to the provider.\n"
                    + "Your job is now PENDING PAYMENT.\n"
                    + "Total: PKR " + String.format("%.2f", generatedInvoice.getTotalAmount()));
            alert.showAndWait();
            MainApp.switchScene("dashboard.fxml");
        } catch (Exception e) {
            previewErrorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleEditFigures() {
        previewPanel.setVisible(false);
        previewPanel.setManaged(false);
        entryPanel.setVisible(true);
        entryPanel.setManaged(true);
        previewErrorLabel.setText("");
        entryErrorLabel.setText("");
    }

    @FXML
    private void handleViewDispute() {
        entryErrorLabel.setText("");
        MicroJob selectedJob = jobCombo.getValue();
        if (selectedJob == null) {
            entryErrorLabel.setText("Please select a job first.");
            return;
        }
        try {
            Invoice invoice = invoiceService.getInvoiceByJobId(selectedJob.getJobId());
            if (invoice == null) {
                entryErrorLabel.setText("No invoice found for this job. The provider has not yet disputed an invoice.");
                return;
            }
            Dispute dispute = paymentService.getDisputeByInvoiceId(invoice.getInvoiceId());
            if (dispute == null) {
                entryErrorLabel.setText("No active dispute found for this job.");
                return;
            }
            if (!dispute.isAwaitingSeekerResponse()) {
                if (Dispute.STATUS_CHALLENGED.equals(dispute.getResolutionStatus())) {
                    entryErrorLabel.setText("Your challenge has been submitted. Waiting for the provider to respond in Review Invoice.");
                } else if (Dispute.STATUS_RESOLVED.equals(dispute.getResolutionStatus())) {
                    entryErrorLabel.setText("This dispute has already been resolved.");
                } else {
                    entryErrorLabel.setText("No dispute awaiting your response for this job.");
                }
                return;
            }
            activeJob = selectedJob;
            activeInvoice = invoice;
            activeDispute = dispute;
            MainApp.switchScene("challenge_dispute.fxml");
        } catch (Exception e) {
            entryErrorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void initReviewInvoice() {
        loadSidebar("reviewInvoice");
        setupReviewInvoiceCombo();
        loadProviderPendingJobs();
        jobCombo.setOnAction(e -> loadInvoiceDetails());
    }

    private void setupReviewInvoiceCombo() {
        jobCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
        jobCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
    }

    private void loadProviderPendingJobs() {
        User currentUser = SessionManager.getCurrentUser();
        try {
            List<MicroJob> jobs = jobService.getJobsByProvider(currentUser.getUserId());
            jobs.removeIf(j -> !MicroJob.STATUS_PENDING_PAYMENT.equals(j.getStatus()));
            jobCombo.setItems(FXCollections.observableArrayList(jobs));
            statusLabel.setText(jobs.isEmpty() ? "No jobs awaiting payment." : "Select a job to view its invoice.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void loadInvoiceDetails() {
        MicroJob selected = jobCombo.getValue();
        if (selected == null) return;
        hideChallengePanel();
        approveBtn.setDisable(true);
        disputeBtn.setDisable(true);
        invoiceDetailsBox.setVisible(false);
        invoiceDetailsBox.setManaged(false);
        try {
            currentInvoice = invoiceService.getInvoiceByJobId(selected.getJobId());
            if (currentInvoice == null) {
                statusLabel.setText("No invoice found for this job.");
                return;
            }
            double labourCost = currentInvoice.getHoursWorked() * selected.getHourlyRate();
            detailJobTitle.setText(selected.getTitle());
            detailHours.setText(currentInvoice.getHoursWorked() + " hrs");
            detailRate.setText("PKR " + selected.getHourlyRate()
                    + " / hr  →  Labour: PKR " + String.format("%.2f", labourCost));
            detailSupply.setText("PKR " + String.format("%.2f", currentInvoice.getSupplyCost()));
            detailTotal.setText("PKR " + String.format("%.2f", currentInvoice.getTotalAmount()));
            invoiceDetailsBox.setVisible(true);
            invoiceDetailsBox.setManaged(true);
            currentDispute = paymentService.getDisputeByInvoiceId(currentInvoice.getInvoiceId());
            if (currentDispute == null) {
                approveBtn.setDisable(false);
                disputeBtn.setDisable(false);
                statusLabel.setText("Review the invoice and choose to approve or dispute.");
            } else if (currentDispute.isAwaitingProviderResponse()) {
                showChallengePanel(currentDispute);
                statusLabel.setText("The seeker has challenged your dispute. Review their counter-argument below.");
            } else if (Dispute.STATUS_RESOLVED.equals(currentDispute.getResolutionStatus())) {
                statusLabel.setText("This dispute has been resolved.");
            } else {
                statusLabel.setText("Dispute raised. Awaiting seeker's response.");
                approveBtn.setDisable(false);
                disputeBtn.setDisable(true);
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void showChallengePanel(Dispute dispute) {
        challengeReasonLabel.setText("Seeker's argument: " + dispute.getLastChallenge());
        challengeAmountLabel.setText("Proposed amount: PKR " + String.format("%.2f", dispute.getProposedAmount()));
        boolean maxReached = dispute.isMaxRoundsReached();
        reDisputeBtn.setDisable(maxReached);
        if (maxReached) {
            challengeAmountLabel.setText(challengeAmountLabel.getText() + "  (Final — re-dispute not available)");
        }
        challengePanel.setVisible(true);
        challengePanel.setManaged(true);
        approveBtn.setDisable(true);
        disputeBtn.setDisable(true);
    }

    private void hideChallengePanel() {
        if (challengePanel != null) {
            challengePanel.setVisible(false);
            challengePanel.setManaged(false);
        }
    }

    @FXML
    private void handleApprove() {
        if (currentInvoice == null) return;
        MicroJob selectedJob = jobCombo.getValue();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Pay PKR " + String.format("%.2f", currentInvoice.getTotalAmount()) + "?");
        confirm.setContentText("This will settle \"" + selectedJob.getTitle() + "\" and mark it as SETTLED.");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    paymentService.settlePayment(currentInvoice.getInvoiceId());
                    showSuccessAlert("Payment of PKR "
                            + String.format("%.2f", currentInvoice.getTotalAmount())
                            + " settled. Job is now SETTLED.");
                    MainApp.switchScene("dashboard.fxml");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleDispute() {
        if (currentInvoice == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dispute Invoice");
        dialog.setHeaderText("Raise a dispute for this invoice");
        dialog.setContentText("Reason for dispute:");
        dialog.showAndWait().ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                statusLabel.setText("Dispute reason cannot be empty.");
                return;
            }
            try {
                paymentService.disputeInvoice(currentInvoice.getInvoiceId(), reason.trim());
                showSuccessAlert("Dispute raised. The seeker has been notified.");
                approveBtn.setDisable(false);
                disputeBtn.setDisable(true);
                statusLabel.setText("Dispute raised. Awaiting seeker's response.");
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleAcceptProposed() {
        if (currentDispute == null || currentDispute.getProposedAmount() == null) return;
        double proposed = currentDispute.getProposedAmount();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Accept Proposed Amount");
        confirm.setHeaderText("Pay the negotiated amount of PKR " + String.format("%.2f", proposed) + "?");
        confirm.setContentText("This settles the dispute and marks the job as SETTLED.");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    paymentService.settleAtProposedAmount(currentInvoice.getInvoiceId(), proposed);
                    showSuccessAlert("Settled at PKR " + String.format("%.2f", proposed) + ". Job is now SETTLED.");
                    MainApp.switchScene("dashboard.fxml");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleReDispute() {
        if (currentInvoice == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Re-Dispute");
        dialog.setHeaderText("You are re-disputing the seeker's counter-argument.");
        dialog.setContentText("Your reason for rejecting their proposed amount:");
        dialog.showAndWait().ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                statusLabel.setText("Reason cannot be empty.");
                return;
            }
            try {
                paymentService.reDispute(currentInvoice.getInvoiceId(), reason.trim());
                showSuccessAlert("Re-dispute submitted. The seeker has been notified.");
                hideChallengePanel();
                statusLabel.setText("Re-disputed. Awaiting seeker's response.");
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        });
    }

    private void showSuccessAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Done");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void initRateReview() {
        loadSidebar("rateReview");
        setupRateReviewCombo();
        setupRatingGroup();
        loadSettledJobs();
        jobCombo.setOnAction(e -> loadReviewPanel());
    }

    private void setupRateReviewCombo() {
        jobCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
        jobCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MicroJob item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });
    }

    private void setupRatingGroup() {
        ratingGroup = new ToggleGroup();
        star1.setToggleGroup(ratingGroup);
        star2.setToggleGroup(ratingGroup);
        star3.setToggleGroup(ratingGroup);
        star4.setToggleGroup(ratingGroup);
        star5.setToggleGroup(ratingGroup);
    }

    private void loadSettledJobs() {
        User currentUser = SessionManager.getCurrentUser();
        try {
            List<MicroJob> jobs;
            if (currentUser.isProvider()) {
                jobs = jobService.getJobsByProvider(currentUser.getUserId());
            } else {
                jobs = jobService.getJobsBySeeker(currentUser.getUserId());
            }
            jobs.removeIf(j -> !MicroJob.STATUS_SETTLED.equals(j.getStatus()));
            jobCombo.setItems(FXCollections.observableArrayList(jobs));
            if (jobs.isEmpty()) {
                statusLabel.setText("No settled jobs found. Jobs must be SETTLED before reviewing.");
            } else {
                statusLabel.setText("Select a job to rate the other party.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading jobs: " + e.getMessage());
        }
    }

    private void loadReviewPanel() {
        MicroJob selected = jobCombo.getValue();
        if (selected == null) return;
        User currentUser = SessionManager.getCurrentUser();
        reviewErrorLabel.setText("");
        alreadyReviewedLabel.setVisible(false);
        alreadyReviewedLabel.setManaged(false);
        ratingGroup.selectToggle(null);
        commentArea.clear();
        submitBtn.setDisable(false);
        try {
            if (currentUser.isProvider()) {
                ApplicationDAO appDAO = new ApplicationDAO();
                int seekerId = appDAO.getAcceptedSeekerId(selected.getJobId());
                UserDAO userDAO = new UserDAO();
                User seeker = userDAO.getUserById(seekerId);
                counterpartyUserId = seekerId;
                counterpartyLabel.setText("Rating: " + seeker.getName() + " (Job Seeker)");
            } else {
                counterpartyUserId = selected.getProviderId();
                UserDAO userDAO = new UserDAO();
                User provider = userDAO.getUserById(counterpartyUserId);
                counterpartyLabel.setText("Rating: " + provider.getName() + " (Job Provider)");
            }
            ReviewDAO reviewDAO = new ReviewDAO();
            boolean alreadyReviewed = reviewDAO.hasReviewed(selected.getJobId(), currentUser.getUserId());
            if (alreadyReviewed) {
                submitBtn.setDisable(true);
                ratingGroup.getToggles().forEach(t -> ((RadioButton) t).setDisable(true));
                commentArea.setDisable(true);
                alreadyReviewedLabel.setText("You have already submitted a review for this job.");
                alreadyReviewedLabel.setVisible(true);
                alreadyReviewedLabel.setManaged(true);
            } else {
                ratingGroup.getToggles().forEach(t -> ((RadioButton) t).setDisable(false));
                commentArea.setDisable(false);
            }
            reviewPanel.setVisible(true);
            reviewPanel.setManaged(true);
            statusLabel.setText("");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            reviewPanel.setVisible(false);
            reviewPanel.setManaged(false);
        }
    }

    @FXML
    private void handleSubmitReview() {
        reviewErrorLabel.setText("");
        MicroJob selectedJob = jobCombo.getValue();
        if (selectedJob == null) return;
        Toggle selected = ratingGroup.getSelectedToggle();
        if (selected == null) {
            reviewErrorLabel.setText("Please select a rating (1–5 stars).");
            return;
        }
        int rating = Integer.parseInt(selected.getUserData().toString());
        String comment = commentArea.getText().trim();
        User currentUser = SessionManager.getCurrentUser();
        try {
            reviewService.submitReview(selectedJob.getJobId(), currentUser.getUserId(), counterpartyUserId, rating, comment);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Review Submitted");
            alert.setHeaderText(null);
            alert.setContentText("Your " + rating + "-star review has been submitted. Thank you!");
            alert.showAndWait();
            MainApp.switchScene("dashboard.fxml");
        } catch (Exception e) {
            reviewErrorLabel.setText(e.getMessage());
        }
    }

    private void initManagePostings() {
        loadSidebar("managePostings");
        setupManagePostingsTable();
        loadAllProviderJobs();
        jobsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    if (selected == null) {
                        editBtn.setDisable(true);
                        cancelBtn.setDisable(true);
                    } else {
                        boolean isOpen = MicroJob.STATUS_OPEN.equals(selected.getStatus());
                        editBtn.setDisable(!isOpen);
                        cancelBtn.setDisable(!isOpen);
                        if (!isOpen) {
                            statusLabel.setText("Only OPEN jobs can be edited or cancelled.");
                        } else {
                            statusLabel.setText("Select Edit to modify or Cancel to withdraw this job.");
                        }
                    }
                    hideEditForm();
                });
    }

    private void setupManagePostingsTable() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        rateCol.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<MicroJob, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "OPEN"            -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        case "IN_PROGRESS"     -> setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                        case "PENDING_PAYMENT" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                        case "SETTLED"         -> setStyle("-fx-text-fill: #7f8c8d;");
                        case "CANCELLED"       -> setStyle("-fx-text-fill: #e74c3c;");
                        default                -> setStyle("");
                    }
                }
            }
        });
        editCategory.setItems(FXCollections.observableArrayList(
                "Cleaning", "Cooking", "Laundry", "Delivery",
                "Gardening", "Plumbing", "Painting", "Other"));
    }

    private void loadAllProviderJobs() {
        User currentUser = SessionManager.getCurrentUser();
        try {
            List<MicroJob> jobs = jobService.getJobsByProvider(currentUser.getUserId());
            jobsTable.setItems(FXCollections.observableArrayList(jobs));
            statusLabel.setText(jobs.size() + " posting(s) found. Select a row to manage it.");
        } catch (Exception e) {
            statusLabel.setText("Error loading postings: " + e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        MicroJob selected = jobsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        jobBeingEdited = selected;
        editTitle.setText(selected.getTitle());
        editCategory.setValue(selected.getCategory());
        editLocation.setText(selected.getLocation());
        editRate.setText(String.valueOf(selected.getHourlyRate()));
        editDescription.setText(selected.getDescription());
        editErrorLabel.setText("");
        editForm.setVisible(true);
        editForm.setManaged(true);
        statusLabel.setText("Edit the fields below and click Save Changes.");
    }

    @FXML
    private void handleSaveEdit() {
        editErrorLabel.setText("");
        if (jobBeingEdited == null) return;
        String title = editTitle.getText().trim();
        String category = editCategory.getValue();
        String location = editLocation.getText().trim();
        String rateText = editRate.getText().trim();
        String description = editDescription.getText().trim();
        if (title.isEmpty() || location.isEmpty() || rateText.isEmpty() || description.isEmpty()) {
            editErrorLabel.setText("All fields are required.");
            return;
        }
        if (category == null) {
            editErrorLabel.setText("Please select a category.");
            return;
        }
        double hourlyRate;
        try {
            hourlyRate = Double.parseDouble(rateText);
            if (hourlyRate <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            editErrorLabel.setText("Hourly rate must be a positive number.");
            return;
        }
        MicroJob updated = new MicroJob();
        updated.setJobId(jobBeingEdited.getJobId());
        updated.setProviderId(jobBeingEdited.getProviderId());
        updated.setTitle(title);
        updated.setCategory(category);
        updated.setLocation(location);
        updated.setHourlyRate(hourlyRate);
        updated.setDescription(description);
        updated.setStatus(jobBeingEdited.getStatus());
        try {
            jobService.editJob(updated);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saved");
            alert.setHeaderText(null);
            alert.setContentText("Job updated successfully.");
            alert.showAndWait();
            hideEditForm();
            jobBeingEdited = null;
            loadAllProviderJobs();
        } catch (Exception e) {
            editErrorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDiscardEdit() {
        hideEditForm();
        jobBeingEdited = null;
        statusLabel.setText("Edit discarded.");
    }

    private void hideEditForm() {
        editForm.setVisible(false);
        editForm.setManaged(false);
        editErrorLabel.setText("");
    }

    @FXML
    private void handleCancelJob() {
        MicroJob selected = jobsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Job Posting");
        confirm.setHeaderText("Cancel \"" + selected.getTitle() + "\"?");
        confirm.setContentText("This will permanently cancel the job posting.\nThis cannot be undone.");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    jobService.cancelJob(selected.getJobId());
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Cancelled");
                    success.setHeaderText(null);
                    success.setContentText("Job posting cancelled.");
                    success.showAndWait();
                    loadAllProviderJobs();
                    statusLabel.setText("Job cancelled.");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    private void initNotifications() {
        embeddedSidebarCtrl = loadSidebar("notifications");
        setupNotificationsListView();
        loadNotifications();
    }

    private void setupNotificationsListView() {
        notifList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NotificationStore.Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("");
                } else {
                    VBox card = new VBox(4);
                    card.setPadding(new Insets(10, 14, 10, 14));
                    String bg = item.isRead()
                            ? "-fx-background-color: white;"
                            : "-fx-background-color: #EEF4FF;";
                    card.setStyle(bg + " -fx-background-radius: 8; " +
                            "-fx-border-color: #dde; -fx-border-radius: 8; " +
                            "-fx-effect: dropshadow(gaussian,#ccc,2,0,0,1);");
                    Label msgLabel = new Label(item.getMessage());
                    msgLabel.setWrapText(true);
                    msgLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #222;");
                    if (!item.isRead()) {
                        msgLabel.setStyle(msgLabel.getStyle() + " -fx-font-weight: bold;");
                    }
                    Label timeLabel = new Label(item.getFormattedTime());
                    timeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #aaa;");
                    card.getChildren().addAll(msgLabel, timeLabel);
                    setGraphic(card);
                    setPrefWidth(0);
                    setStyle("-fx-padding: 4 8; -fx-background-color: transparent;");
                }
            }
        });
    }

    private void loadNotifications() {
        List<NotificationStore.Notification> all = NotificationStore.getAll();
        notifList.getItems().setAll(all);
        int unread = NotificationStore.getUnreadCount();
        if (unread > 0) {
            unreadCountLabel.setText(unread + " new");
            unreadCountLabel.setVisible(true);
        } else {
            unreadCountLabel.setVisible(false);
        }
    }

    @FXML
    private void handleRefresh() {
        // Clear current in-memory notifications and reload fresh from DB
        NotificationStore.clear();
        SessionManager.loadSessionNotifications(SessionManager.getCurrentUser());

        // Reload the list view and badges
        loadNotifications();
        if (embeddedSidebarCtrl != null) embeddedSidebarCtrl.refreshBadges();
    }

    @FXML
    private void handleMarkAllRead() {
        NotificationStore.markAllRead();
        loadNotifications();
        if (embeddedSidebarCtrl != null) embeddedSidebarCtrl.refreshBadges();
    }

    private void initProfile() {
        loadSidebar("profile");
        errorLabel.setText("");
        User currentUser = SessionManager.getCurrentUser();
        nameLabel.setText(currentUser.getName());
        emailLabel.setText(currentUser.getEmail());
        roleLabel.setText(currentUser.getRoleLabel());
        ratingLabel.setText(String.format("%.2f / 5.00", currentUser.getAverageRating()));
        bioArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");
        contactField.setText(currentUser.getContactDetails() != null ? currentUser.getContactDetails() : "");
        boolean isSeeker = currentUser instanceof JobSeeker;
        skillsHeaderLabel.setVisible(isSeeker);
        skillsHeaderLabel.setManaged(isSeeker);
        skillsField.setVisible(isSeeker);
        skillsField.setManaged(isSeeker);
        if (isSeeker) {
            List<SkillTag> tags = ((JobSeeker) currentUser).getSkillTags();
            if (tags != null && !tags.isEmpty()) {
                skillsField.setText(tags.stream().map(SkillTag::getTagName).collect(Collectors.joining(", ")));
            }
        }
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");
        User currentUser = SessionManager.getCurrentUser();
        String bio = bioArea.getText().trim();
        String contact = contactField.getText().trim();
        List<String> skillTagNames = null;
        if (currentUser instanceof JobSeeker) {
            String skillsText = skillsField.getText().trim();
            if (!skillsText.isEmpty()) {
                skillTagNames = Arrays.stream(skillsText.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        }
        try {
            new UserService().updateProfile(currentUser.getUserId(), bio, contact, skillTagNames);
            currentUser.setBio(bio);
            currentUser.setContactDetails(contact);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Updated");
            alert.setHeaderText(null);
            alert.setContentText("Your profile has been saved successfully.");
            alert.showAndWait();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleViewReviews() {
        MainApp.switchScene("my_reviews.fxml");
    }

    private void initMyReviews() {
        loadSidebar("profile");
        setupMyReviewsTable();
        loadMyReviews();
    }

    private void setupMyReviewsTable() {
        reviewerCol.setCellValueFactory(new PropertyValueFactory<>("reviewerName"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingCol.setStyle("-fx-alignment: CENTER;");
        starsCol.setCellFactory(col -> new TableCell<Review, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Review r = (Review) getTableRow().getItem();
                    setText("★".repeat(r.getRating()) + "☆".repeat(5 - r.getRating()));
                    setStyle("-fx-text-fill: #f39c12; -fx-font-size: 13;");
                }
            }
        });
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentCol.setCellFactory(col -> {
            TableCell<Review, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item.length() > 60 ? item.substring(0, 57) + "..." : item);
                        setTooltip(new Tooltip(item));
                    }
                }
            };
            return cell;
        });
        dateCol.setCellFactory(col -> new TableCell<Review, Object>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Review r = (Review) getTableRow().getItem();
                    setText(r.getReviewDate() != null ? r.getReviewDate().format(fmt) : "—");
                }
            }
        });
    }

    private void loadMyReviews() {
        User currentUser = SessionManager.getCurrentUser();
        overallRatingLabel.setText(String.format("%.2f", currentUser.getAverageRating()));
        roleLabel.setText(currentUser.getRoleLabel());
        try {
            List<Review> reviews = reviewService.getReviewsForUser(currentUser.getUserId());
            totalReviewsLabel.setText(String.valueOf(reviews.size()));
            if (reviews.isEmpty()) {
                statusLabel.setText("You have not received any reviews yet.");
                reviewsTable.setItems(FXCollections.observableArrayList());
            } else {
                reviewsTable.setItems(FXCollections.observableArrayList(reviews));
                statusLabel.setText(reviews.size() + " review(s) received.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading reviews: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initChallengeDispute() {
        loadSidebar("completeJob");
        errorLabel.setText("");
        populateDisputeInfo();
    }

    private void populateDisputeInfo() {
        if (activeJob == null || activeInvoice == null || activeDispute == null) {
            errorLabel.setText("No active dispute found. Please return to the dashboard.");
            return;
        }
        jobTitleLabel.setText(activeJob.getTitle());
        disputeReasonLabel.setText(activeDispute.getReason());
        invoiceTotalLabel.setText("PKR " + String.format("%.2f", activeInvoice.getTotalAmount()));
        int round = activeDispute.getRoundCount() + 1;
        roundLabel.setText("Round " + round + " of " + Dispute.MAX_ROUNDS);
        proposedAmountField.setText(String.format("%.2f", activeInvoice.getTotalAmount()));
    }

    @FXML
    private void handleSubmitChallenge() {
        errorLabel.setText("");
        if (activeDispute == null || activeInvoice == null) {
            errorLabel.setText("No active dispute.");
            return;
        }
        String challengeText = challengeArea.getText().trim();
        String amountText = proposedAmountField.getText().trim();
        if (challengeText.isEmpty()) {
            errorLabel.setText("Please enter your counter-argument.");
            return;
        }
        if (amountText.isEmpty()) {
            errorLabel.setText("Please enter your proposed settlement amount.");
            return;
        }
        double proposed;
        try {
            proposed = Double.parseDouble(amountText);
            if (proposed <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Proposed amount must be a positive number.");
            return;
        }
        try {
            paymentService.challengeDispute(activeDispute.getDisputeId(), challengeText, proposed);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Challenge Submitted");
            alert.setHeaderText(null);
            alert.setContentText("Your challenge has been submitted.\n"
                    + "The provider has been notified and will respond in Review Invoice.");
            alert.showAndWait();
            activeJob = null;
            activeInvoice = null;
            activeDispute = null;
            MainApp.switchScene("dashboard.fxml");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleAcceptDispute() {
        if (activeJob == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Accept Dispute");
        confirm.setHeaderText("Accept the provider's dispute?");
        confirm.setContentText("The provider's dispute will be resolved. The job will be settled. This cannot be undone.");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Dispute d = paymentService.getDisputeByInvoiceId(activeInvoice.getInvoiceId());
                    if (d != null) {
                        new DisputeDAO().resolveDispute(d.getDisputeId());
                        new com.quickhire.dao.JobDAO().updateJobStatus(activeJob.getJobId(), MicroJob.STATUS_SETTLED);
                    }
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Dispute Accepted");
                    info.setHeaderText(null);
                    info.setContentText("You have accepted the dispute. Job is now SETTLED.");
                    info.showAndWait();
                    activeJob = null;
                    activeInvoice = null;
                    activeDispute = null;
                    MainApp.switchScene("dashboard.fxml");
                } catch (Exception e) {
                    errorLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    public void initUser(User user) {
        lblUserName.setText(user.getName());
        if (avatarLabel != null) {
            avatarLabel.setText(String.valueOf(user.getName().charAt(0)).toUpperCase());
        }
        lblUserRole.setText(user.getRoleLabel());
        if (user.isSeeker()) {
            seekerNav.setVisible(true);
            seekerNav.setManaged(true);
            providerNav.setVisible(false);
            providerNav.setManaged(false);
            if (applicantBadge != null) applicantBadge.setVisible(false);
        } else {
            seekerNav.setVisible(false);
            seekerNav.setManaged(false);
            providerNav.setVisible(true);
            providerNav.setManaged(true);
        }
        refreshBadges();
    }

    public void refreshBadges() {
        boolean hasUnread = NotificationStore.hasUnread();
        notifBadge.setVisible(hasUnread);
        if (applicantBadge != null && SessionManager.getCurrentUser() != null
                && SessionManager.getCurrentUser().isProvider()) {
            long pendingCount = NotificationStore.getAll().stream()
                    .filter(n -> !n.isRead() && n.getMessage().startsWith("📋"))
                    .count();
            applicantBadge.setVisible(pendingCount > 0);
        }
    }

    public void setActiveButton(String screenName) {
        clearActiveStyles();
        switch (screenName) {
            case "dashboard"        -> btnDashboard.getStyleClass().add("nav-button-active");
            case "notifications"    -> btnNotifications.getStyleClass().add("nav-button-active");
            case "findJobs"         -> btnFindJobs.getStyleClass().add("nav-button-active");
            case "updateProgress"   -> btnUpdateProgress.getStyleClass().add("nav-button-active");
            case "completeJob"      -> btnCompleteJob.getStyleClass().add("nav-button-active");
            case "postJob"          -> btnPostJob.getStyleClass().add("nav-button-active");
            case "reviewApplicants" -> btnReviewApplicants.getStyleClass().add("nav-button-active");
            case "managePostings"   -> btnManagePostings.getStyleClass().add("nav-button-active");
            case "reviewInvoice"    -> btnReviewInvoice.getStyleClass().add("nav-button-active");
            case "rateReview"       -> btnRateReview.getStyleClass().add("nav-button-active");
            case "profile"          -> btnProfile.getStyleClass().add("nav-button-active");
        }
    }

    private void clearActiveStyles() {
        for (Button b : new Button[]{btnDashboard, btnNotifications, btnFindJobs,
                btnUpdateProgress, btnCompleteJob, btnPostJob, btnReviewApplicants,
                btnManagePostings, btnReviewInvoice, btnRateReview, btnProfile}) {
            if (b != null) b.getStyleClass().remove("nav-button-active");
        }
    }

    @FXML private void handleDashboard()        { navigateSidebar("dashboard"); }
    @FXML private void handleNotifications()    { navigateSidebar("notifications"); }
    @FXML private void handleFindJobs()         { navigateSidebar("findJobs"); }
    @FXML private void handleUpdateProgress()   { navigateSidebar("updateProgress"); }
    @FXML private void handleCompleteJob()      { navigateSidebar("completeJob"); }
    @FXML private void handlePostJob()          { navigateSidebar("postJob"); }
    @FXML private void handleReviewApplicants() { navigateSidebar("reviewApplicants"); }
    @FXML private void handleManagePostings()   { navigateSidebar("managePostings"); }
    @FXML private void handleReviewInvoice()    { navigateSidebar("reviewInvoice"); }
    @FXML private void handleRateReview()       { navigateSidebar("rateReview"); }
    @FXML private void handleProfile()          { navigateSidebar("profile"); }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        MainApp.switchScene("login.fxml");
    }

    private void navigateSidebar(String screen) {
        setActiveButton(screen);
        switch (screen) {
            case "dashboard"        -> MainApp.switchScene("dashboard.fxml");
            case "notifications"    -> MainApp.switchScene("notifications.fxml");
            case "findJobs"         -> MainApp.switchScene("find_jobs.fxml");
            case "updateProgress"   -> MainApp.switchScene("update_progress.fxml");
            case "completeJob"      -> MainApp.switchScene("complete_job.fxml");
            case "postJob"          -> MainApp.switchScene("post_job.fxml");
            case "reviewApplicants" -> MainApp.switchScene("review_applicants.fxml");
            case "managePostings"   -> MainApp.switchScene("manage_postings.fxml");
            case "reviewInvoice"    -> MainApp.switchScene("review_invoice.fxml");
            case "rateReview"       -> MainApp.switchScene("rate_review.fxml");
            case "profile"          -> MainApp.switchScene("profile.fxml");
        }
    }

    @FXML
    private void handleBack() {
        if (jobDescArea != null || noteArea != null && updatesListView == null) {
            MainApp.switchScene("find_jobs.fxml");
        } else if (reviewsTable != null) {
            MainApp.switchScene("profile.fxml");
        } else {
            MainApp.switchScene("dashboard.fxml");
        }
    }
}
