SET @now = '2017-07-01 11:59:00';
SET @nowplus1 = '2017-07-02 11:59:00';
SET @nowplus2 = '2017-07-03 11:59:00';
SET @nowplus3 = '2017-07-04 11:59:00';
SET @nowplus4 = '2017-07-05 11:59:00';
SET @nowplus5 = '2017-07-06 11:59:00';

-- statuses
SET @SP  = 'Selected for production';
SET @IA  = 'Inspect - Attempt';
SET @WI  = 'Withdrawn';
SET @GC  = 'Genotype confirmed mice';
SET @ST  = 'Started';
SET @J   = 'Junk';

INSERT INTO contact (pk, address, inHtml, created_at) VALUES
  (1, 'user1@ebi.ac.uk', 1, @now),
  (2, 'user2@ebi.ac.uk', 0, @now),
  (3, 'user3@ebi.ac.uk', 1, @now),
  (4, 'user4@ebi.ac.uk', 0, @now)
;

INSERT INTO contact_gene
(pk,  contact_pk, gene_accession_id, created_at) VALUES
  (1,  1,          'MGI:103576',      @now),
  (2,  1,          'MGI:1919199',     @now),
  (3,  1,          'MGI:2443658',     @now),
  (4,  2,          'MGI:2444824',     @now),
  (5,  2,          'MGI:3576659',     @now),

  (6,  3,          'MGI:87986',       @now),
  (7,  3,          'MGI:1914855',     @now),
  (8,  3,          'MGI:104874',      @now),
  (9,  3,          'MGI:3693832',     @now);

INSERT INTO gene_sent
( pk,  address,           gene_accession_id, symbol,     assignment_status, conditional_allele_production_status, null_allele_production_status, crispr_allele_production_status, phenotyping_data_available, created_at, sent_at) VALUES
  (1,  'user1@ebi.ac.uk', 'MGI:103576',      'Ccl11',    NULL,              @ST,                                  NULL,                          NULL,                            NULL,                       NOW(),      NOW()),
  (2,  'user1@ebi.ac.uk', 'MGI:1919199',     'Cers5',    @IA,               NULL,                                 @ST,                           NULL,                            0,                          NOW(),      NOW()),
  (3,  'user1@ebi.ac.uk', 'MGI:2443658',     'Prr14l',   @SP,               NULL,                                 NULL,                          @GC,                             1,                          NOW(),      NOW()),
  (4,  'user2@ebi.ac.uk', 'MGI:2444824',     'Sirpb1a',  @SP,               NULL,                                 @GC,                           @ST,                             1,                          NOW(),      NOW()),
  (5,  'user2@ebi.ac.uk', 'MGI:3576659',     'Ano5',     NULL,              @WI,                                  NULL,                          @SP,                             NULL,                       NOW(),      NOW()),

  (6,  'user3@ebi.ac.uk', 'MGI:87986',       'Akt1',     @SP,               NULL,                                 NULL,                         NULL,                             0,                          NOW(),      NOW()),
  (7,  'user3@ebi.ac.uk', 'MGI:1914855',     'Akt1s1',   'xxxxx',          'xxxxx',                               'xxxxx',                      'xxxxx',                          1,                          NOW(),      NOW()),
  (8,  'user3@ebi.ac.uk', 'MGI:104874',      'Akt2',     @SP,              'xxxxx',                               NULL,                         'xxxxx',                          1,                          NOW(),      NOW()),
  (9,  'user3@ebi.ac.uk', 'MGI:3693832',     'Aktip',    'xxxxx',          @GC,                                   'xxxxx',                      NULL,                             NULL,                       NOW(),      NOW())
;