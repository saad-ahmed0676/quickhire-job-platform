-- ============================================================
-- QuickHire — Database Schema (Microsoft SQL Server)
-- ============================================================
-- Run this script against a fresh database to create all tables
-- required by the application. Tested against SQL Server / SQL
-- Server Express with the mssql-jdbc driver.
--
-- Usage:
--   1. Create the database:   CREATE DATABASE QuickHireDB;
--   2. Switch to it:          USE QuickHireDB;
--   3. Run this entire script.
-- ============================================================

IF DB_ID('QuickHireDB') IS NULL
BEGIN
    CREATE DATABASE QuickHireDB;
END
GO

USE QuickHireDB;
GO

-- ------------------------------------------------------------
-- Users (base table for both Job Seekers and Job Providers,
-- distinguished by the `role` column)
-- ------------------------------------------------------------
CREATE TABLE Users (
    userId          INT IDENTITY(1,1) PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    passwordHash    VARCHAR(256)    NOT NULL,
    role            VARCHAR(20)     NOT NULL CHECK (role IN ('SEEKER', 'PROVIDER')),
    bio             VARCHAR(MAX)    NULL,
    contactDetails  VARCHAR(200)    NULL,
    averageRating   FLOAT           NOT NULL DEFAULT 0,
    createdAt       DATETIME        NOT NULL DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- MicroJobs (job postings created by Providers)
-- ------------------------------------------------------------
CREATE TABLE MicroJobs (
    jobId           INT IDENTITY(1,1) PRIMARY KEY,
    providerId      INT             NOT NULL FOREIGN KEY REFERENCES Users(userId),
    title           VARCHAR(150)    NOT NULL,
    category        VARCHAR(50)     NULL,
    description     VARCHAR(MAX)    NULL,
    location        VARCHAR(100)    NULL,
    hourlyRate      FLOAT           NOT NULL DEFAULT 0,
    status          VARCHAR(20)     NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'CLOSED')),
    postedDate      DATETIME        NOT NULL DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- JobApplications (Seekers applying to MicroJobs)
-- ------------------------------------------------------------
CREATE TABLE JobApplications (
    applicationId    INT IDENTITY(1,1) PRIMARY KEY,
    jobId            INT            NOT NULL FOREIGN KEY REFERENCES MicroJobs(jobId),
    seekerId         INT            NOT NULL FOREIGN KEY REFERENCES Users(userId),
    applicationNote  VARCHAR(MAX)   NULL,
    submissionDate   DATETIME       NOT NULL DEFAULT GETDATE(),
    status           VARCHAR(20)    NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);
GO

-- ------------------------------------------------------------
-- Reviews (rating + comment left by one user about another,
-- tied to a completed job)
-- ------------------------------------------------------------
CREATE TABLE Reviews (
    reviewId        INT IDENTITY(1,1) PRIMARY KEY,
    jobId           INT             NOT NULL FOREIGN KEY REFERENCES MicroJobs(jobId),
    reviewerId      INT             NOT NULL FOREIGN KEY REFERENCES Users(userId),
    revieweeId      INT             NOT NULL FOREIGN KEY REFERENCES Users(userId),
    rating          INT             NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment         VARCHAR(MAX)    NULL,
    reviewDate      DATETIME        NOT NULL DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- Invoices (generated once a job is completed; one per job)
-- ------------------------------------------------------------
CREATE TABLE Invoices (
    invoiceId           INT IDENTITY(1,1) PRIMARY KEY,
    jobId               INT          NOT NULL UNIQUE FOREIGN KEY REFERENCES MicroJobs(jobId),
    hoursWorked         FLOAT        NOT NULL DEFAULT 0,
    supplyCost          FLOAT        NOT NULL DEFAULT 0,
    totalAmount         FLOAT        NOT NULL DEFAULT 0,
    dateGenerated       DATETIME     NOT NULL DEFAULT GETDATE(),
    confirmedBySeeker   BIT          NOT NULL DEFAULT 0
);
GO

-- ------------------------------------------------------------
-- Payments (one payment record per settled invoice)
-- ------------------------------------------------------------
CREATE TABLE Payments (
    paymentId       INT IDENTITY(1,1) PRIMARY KEY,
    invoiceId       INT             NOT NULL FOREIGN KEY REFERENCES Invoices(invoiceId),
    amount          FLOAT           NOT NULL,
    paymentMethod   VARCHAR(50)     NULL,
    paymentDate     DATETIME        NOT NULL DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- Disputes (raised against an invoice; supports a back-and-forth
-- challenge/re-dispute cycle between Seeker and Provider)
-- ------------------------------------------------------------
CREATE TABLE Disputes (
    disputeId           INT IDENTITY(1,1) PRIMARY KEY,
    invoiceId           INT             NOT NULL FOREIGN KEY REFERENCES Invoices(invoiceId),
    reason              VARCHAR(MAX)    NULL,
    raisedDate          DATETIME        NOT NULL DEFAULT GETDATE(),
    resolutionStatus    VARCHAR(20)     NOT NULL DEFAULT 'OPEN' CHECK (resolutionStatus IN ('OPEN', 'CHALLENGED', 'RE_DISPUTED', 'RESOLVED')),
    roundCount          INT             NOT NULL DEFAULT 0,
    raisedBy            VARCHAR(20)     NULL CHECK (raisedBy IN ('SEEKER', 'PROVIDER')),
    lastChallenge       VARCHAR(MAX)    NULL,
    proposedAmount      FLOAT           NULL
);
GO

-- ------------------------------------------------------------
-- ProgressUpdates (status notes posted while a job is in progress)
-- ------------------------------------------------------------
CREATE TABLE ProgressUpdates (
    updateId        INT IDENTITY(1,1) PRIMARY KEY,
    jobId           INT             NOT NULL FOREIGN KEY REFERENCES MicroJobs(jobId),
    note            VARCHAR(MAX)    NULL,
    timestamp       DATETIME        NOT NULL DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- SkillTags (skills associated with a user profile)
-- ------------------------------------------------------------
CREATE TABLE SkillTags (
    tagId           INT IDENTITY(1,1) PRIMARY KEY,
    userId          INT             NOT NULL FOREIGN KEY REFERENCES Users(userId),
    tagName         VARCHAR(50)     NOT NULL
);
GO

PRINT 'QuickHire schema created successfully.';
