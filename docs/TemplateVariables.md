## Template Variables



#### Athlete information

| Variable                       | Notes                                                        |
| ------------------------------ | ------------------------------------------------------------ |
| 20kgRuleValue                  | 20 for IWF competition, 15 or 10 for Masters, if the Initial Total rule is in effect. |
| age                            | Age as of current date (beware, not the date of competition) |
| ageGroup                       |                                                              |
| attemptedLifts                 | same as attemptsDone                                         |
| attemptNumber                  | Attempt number for current lift (1..3)                       |
| attemptsDone                   | Lifts attempted (0..6)                                       |
| bestCleanJerk                  |                                                              |
| bestCleanJerkAttemptNumber     | (1..3)                                                       |
| bestResultAttemptNumber        | (1..6) used when reaching back to snatch is necessary to tie break |
| bestSnatch                     |                                                              |
| bestSnatchAttemptNumber        | (1..3)                                                       |
| bodyWeight                     |                                                              |
| bWCategory                     | Category limit without gender (example: 67  or >109)         |
| category                       | Full category name including age group and gender            |
| categorySinclair               | Sinclair calculated at bodyweight category instead of bodyweight |
| cleanJerk1ActualLift           | positive if lift was good, negative if lift was failed, 0 if declined by athlete, empty if not yet taken. |
| cleanJerk1AsInteger            | same as actual lift except null if not taken                 |
| cleanJerk1AutomaticProgression |                                                              |
| cleanJerk1Change1              |                                                              |
| cleanJerk1Change2              |                                                              |
| cleanJerk1Declaration          |                                                              |
| cleanJerk1LiftTime             |                                                              |
| cleanJerk2ActualLift           | positive if lift was good, negative if lift was failed, 0 if declined by athlete, empty if not yet taken. |
| cleanJerk2AsInteger            |                                                              |
| cleanJerk2AutomaticProgression |                                                              |
| cleanJerk2Change1              |                                                              |
| cleanJerk2Change2              |                                                              |
| cleanJerk2Declaration          |                                                              |
| cleanJerk2LiftTime             |                                                              |
| cleanJerk3ActualLift           | positive if lift was good, negative if lift was failed, 0 if declined by athlete, empty if not yet taken. |
| cleanJerk3AsInteger            |                                                              |
| cleanJerk3AutomaticProgression |                                                              |
| cleanJerk3Change1              |                                                              |
| cleanJerk3Change2              |                                                              |
| cleanJerk3Declaration          |                                                              |
| cleanJerk3LiftTime             |                                                              |
| cleanJerkAttemptsDone          |                                                              |
| cleanJerkPoints                |                                                              |
| cleanJerkRank                  |                                                              |
| cleanJerkTotal                 |                                                              |
| club                           | same as team (team should be preferred)                      |
| combinedPoints                 |                                                              |
| currentAutomatic               |                                                              |
| currentChange1                 |                                                              |
| currentDeclaration             |                                                              |
| customPoints                   | Not yet in owlcms4                                           |
| customRank                     | Not yet in owlcms4                                           |
| customScore                    | Not yet in owlcms4 -- used to override the total by using a formula (e.g. technical points for certain kid competitions, bonus points based on local traditions, etc.) |
| displayCategory                | same as category, but safe to use if athletes have not all registered yet. |
| firstAttemptedLiftTime         |                                                              |
| firstName                      |                                                              |
| forcedAsCurrent                | not relevant for reports                                     |
| formattedBirth                 | should be used for Excel if you need the full birth date (as on a starting/registration list) -- use yearOfBirth if dealing with a narrow column and where the full birth date is not essential. |
| fullBirthDate                  | see formattedBirth                                           |
| fullId                         | full identification of the athlete with start number and category, as a single string |
| fullName                       | lastname, firstname as a single string                       |
| gender                         | M(ale) or F(emale)                                           |
| group                          | the athlete's group                                          |
| lastAttemptedLiftTime          |                                                              |
| lastName                       |                                                              |
| lastSuccessfulLiftTime         |                                                              |
| liftOrderRank                  |                                                              |
| longCategory                   | same as displayCategory                                      |
| lotNumber                      |                                                              |
| mastersAgeGroup                | legacy.  can be used to display the ageGroup                 |
| mastersAgeGroupInterval        | age boundaries for the age group.  works for all ageGroups   |
| mastersGenderAgeGroupInterval  | same as mastersAgeGroupInterval but with the Gender          |
| mastersLongCategory            | same as displayCategory                                      |
| medalRank                      | 1..3 or empty, based on Total                                |
| membership                     |                                                              |
| nextAttemptRequestedWeight     |                                                              |
| presumedBodyWeight             | body weight as inferred from the category (if not weighed-in) or the actual bodyweight (if weighed-in) |
| previousLiftTime               |                                                              |
| qualifyingTotal                | currently ambiguous.  the same field is also used as the entry total for the 20kg rule. |
| rank                           | rank according to total.  for the protocol sheet, rank within the current group (will be fixed) |
| registrationCategory           | obsolete -- same as category                                 |
| robi                           |                                                              |
| robiRank                       |                                                              |
| shortCategory                  | obsolete - use bWCategory                                    |
| sinclair                       |                                                              |
| sinclairFactor                 |                                                              |
| sinclairForDelta               | kg needed to get to best sinclair ranking                    |
| sinclairPoints                 |                                                              |
| sinclairRank                   |                                                              |
| smm                            |                                                              |
| snatch1ActualLift              | positive if lift was good, negative if lift was failed, 0 if declined by athlete, empty if not yet taken. |
| snatch1AsInteger               |                                                              |
| snatch1AutomaticProgression    |                                                              |
| snatch1Change1                 |                                                              |
| snatch1Change2                 |                                                              |
| snatch1Declaration             |                                                              |
| snatch1LiftTime                |                                                              |
| snatch2ActualLift              | positive if lift was good, negative if lift was failed, 0 if declined by athlete, empty if not yet taken. |
| snatch2AsInteger               |                                                              |
| snatch2AutomaticProgression    |                                                              |
| snatch2Change1                 |                                                              |
| snatch2Change2                 |                                                              |
| snatch2Declaration             |                                                              |
| snatch2LiftTime                |                                                              |
| snatch3ActualLift              | positive if lift was good, negative if lift was failed, 0 if declined by athlete, empty if not yet taken. |
| snatch3AsInteger               |                                                              |
| snatch3AutomaticProgression    |                                                              |
| snatch3Change1                 |                                                              |
| snatch3Change2                 |                                                              |
| snatch3Declaration             |                                                              |
| snatch3LiftTime                |                                                              |
| snatchAttemptsDone             | 0..3                                                         |
| snatchPoints                   |                                                              |
| snatchRank                     |                                                              |
| snatchTotal                    |                                                              |
| startNumber                    |                                                              |
| team                           |                                                              |
| teamCleanJerkRank              | unused so far - computed by Excel                            |
| teamCombinedRank               | unused so far - computed by Excel                            |
| teamMember                     |                                                              |
| teamRobiRank                   |                                                              |
| teamSinclairRank               | unused so far - computed by Excel                            |
| teamSnatchRank                 | unused so far - computed by Excel                            |
| teamTotalRank                  | unused so far - computed by Excel                            |
| total                          |                                                              |
| totalPoints                    |                                                              |
| totalRank                      |                                                              |
| yearOfBirth                    | 4-digit year of birth                                        |
| isEligibleForIndividualRanking | false if athlete is competing out-of-competition (as invited lifter from another state in a state championship, for example) |
| isEligibleForTeamRanking       | unused so far                                                |