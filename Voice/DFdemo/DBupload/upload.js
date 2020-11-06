'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const {WebhookClient} = require('dialogflow-fulfillment');

process.env.DEBUG = 'dialogflow:*'; // enables lib debugging statements
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

// Variables shared by multiple intents
var instructionCount = 1;         // Number instruction user is currently on
var ingredientCount = 1;          // Number ingredient user is currently on
var recipe;                       // Name of recipe user is making
var changeNum;                    // Number of instruction that user is changing
var ingredientLength;             // Number of ingredients in recipe
var instructionLength;            // Number of instructions in recipe

// Variables for changing ingredient after instruction change
var ingredFood;
var ingredNum;
var ingredWeight;

exports.dialogflowFirebaseFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });

  // Assigns database variable depending on title of recipe
  // If no recipe is specified, assign it to default - which prints error messages when referenced

  try {
    const recipeTitle = agent.parameters.recipeTitle;
    if (recipeTitle == 'Peanut Butter Cups')
    {
      recipe = 'PeanutButterCups';
    }
    else if ((recipeTitle == 'Beef and Broccoli') || (recipeTitle == 'Beef and Broccoli Stir Fry'))
    {
      recipe = 'BeefandBroccoli';
    }
  } catch (err) {
    recipe = 'Test-123';
  }

  if (recipe == null) {
    recipe = 'Test-123';
  }

  //recipe = 'demoRecipe';  // For testing purposes

  // Test function to write to database
  function writeToDb (agent) {
    // Get parameter from Dialogflow with the string to add to the database
    const databaseEntry = agent.parameters.databaseEntry;

    // Get the database collection 'dialogflow' and document 'agent' and store
    // the document  {entry: "<value of database entry>"} in the 'agent' document
    const dialogflowAgentRef = db.collection('dialogflow').doc('agent');
    return db.runTransaction(t => {
      t.set(dialogflowAgentRef, {entry: databaseEntry}, {merge: true});
      return Promise.resolve('Write complete');
    }).then(doc => {
      agent.add(`Wrote "${databaseEntry}" to the Firestore database.`);
    }).catch(err => {
      console.log(`Error writing to Firestore: ${err}`);
      agent.add(`Failed to write "${databaseEntry}" to the Firestore database.`);
    });
  }

  // Test function to read from database
  function readFromDb (agent) {
    // Get the database collection 'dialogflow' and document 'agent'
    const dialogflowAgentDoc = db.collection('dialogflow').doc('agent');

    // Get the value of 'entry' in the document and send it to the user
    return dialogflowAgentDoc.get()
      .then(doc => {
        if (!doc.exists) {
          agent.add('No data found in the database!');
        } else {
          agent.add(entry);
        }
        return Promise.resolve('Read complete');
      }).catch(() => {
        agent.add('Error reading entry from the Firestore database.');
        agent.add('Please add a entry to the database first by saying, "Write <your phrase> to the database"');
      });
  }

  // Intent to begin recipe by retrieving title
  function recipeStart (agent) {

    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);    // This provides a referene to what document in the database we want

    return dialogflowAgentDoc.get()
      .then(doc => {
        if (!doc.exists) {
          agent.add('No data for recipe found. Please try another recipe.');      // agent.add provides the response the dialogflow agent will give the user
        } else {
          agent.add('This recipe is for ' + doc.data().title);
          agent.add('Would you like to hear the servings, time, ingredients, or instructions for this recipe?');
        }
        return Promise.resolve('Read complete');
      }).catch(() => {
        agent.add('No data for recipe found. Please try another recipe.');
      });
  }

  // Intent to return recipe servings
  function recipeServe (agent) {
    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);

    return dialogflowAgentDoc.get()
      .then(doc => {
        if (!doc.exists) {
          agent.add('No data for recipe found. Please try another recipe.');
        } else {
          agent.add('This recipe makes: ' + doc.data().servings);
          agent.add('Would you like to hear the time, ingredients, or instructions for this recipe?');
        }
        return Promise.resolve('Read complete');
      }).catch(() => {
        agent.add('No data for recipe found. Please try another recipe.');
      });
  }

  // Intent to return recipe time
  function recipeTime (agent) {
    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);

    return dialogflowAgentDoc.get()
      .then(doc => {
        if (!doc.exists) {
          agent.add('No data for recipe found. Please try another recipe.');
        } else {
          agent.add('This recipe takes: ' + doc.data().time);
          agent.add('Would you like to hear the servings, ingredients, or instructions for this recipe?');
        }
        return Promise.resolve('Read complete');
      }).catch(() => {
        agent.add('No data for recipe found. Please try another recipe.');
      });
  }

  // Intent to start instructions
  function instructionStart (agent) {

    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);

    return dialogflowAgentDoc.get()
      .then(doc => {
        if (!doc.exists) {
          agent.add('No data for recipe found. Please try another recipe.');
        } else {
          agent.add('The first instruction is: ');
          agent.add(doc.data().instruction.step1.procedure);
          instructionCount++;
          agent.add('Next?');
        }
        return Promise.resolve('Read complete');
      }).catch(() => {
        agent.add('No data for recipe found. Please try another recipe.');
      });
  }

  // Intent to start ingredients
  function ingredientStart (agent) {

    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);

    return dialogflowAgentDoc.get()
      .then(doc => {
        if (!doc.exists) {
          agent.add('No data for recipe found. Please try another recipe.');
        } else {
          agent.add('The first ingredient is: ');
          agent.add(doc.data().ingredient.i1);
          ingredientCount++;
          agent.add('Next?');
        }
        return Promise.resolve('Read complete');
      }).catch(() => {
        agent.add('No data for recipe found. Please try another recipe.');
      });
  }

  // Intent to jump to a specific instruction
  function instructionJump (agent) {

    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);

    try {
      const stepNum = agent.parameters.stepNum;
      var num = 0;

      return dialogflowAgentDoc.get()
        .then(doc => {
          if (!doc.exists) {
            agent.add('This recipe does not exist. Please try another recipe.');
          }
          else {
            instructionLength = doc.data().numbers.instructions;
            // Converts user input to indexing number
            switch (stepNum) {
              case 'first':
                num = 1;
                break;
              case 'second':
                num = 2;
                break;
              case 'third':
                num = 3;
                break;
              case 'fourth':
                num = 4;
                break;
              case 'fifth':
                num = 5;
                break;
              case 'sixth':
                num = 6;
                break;
              case 'seventh':
                num = 7;
                break;
              case 'eighth':
                num = 8;
                break;
              case 'ninth':
                num = 9;
                break;
              case 'tenth':
                num = 10;
                break;
              case 'last':
                num = instructionLength;
                break;
              case 'next':
                instructionCount++;
                num = instructionCount;
                break;
              case 'repeat':
                num = instructionCount;
                break;
              case 'back':
                if (instructionCount == 1) {
                  num = 1;
                }
                else {
                  instructionCount--;
                  num = instructionCount;
                }
                break;
            }

            if (num > instructionLength) {
              agent.add('The recipe only has ' + instructionLength + ' instructions.');
              agent.add('Please clarify which instruction you would like');
            }
            else if (num == 0)
            {
              agent.add("That is not a valid instruction. Please clarify which instruction you would like.");
            }
            else {
              var key = 'step' + num;
              agent.add(doc.data().instruction[key].procedure);
              instructionCount = num;
              agent.add('Next?');
            }

          }
          return Promise.resolve('Read complete');
        }).catch(() => {
          agent.add('No data for recipe found. Please try another recipe.');
        });
    }
    catch (err) {
      agent.add('The recipe only has ' + instructionLength + ' instructions.');
      agent.add('Please clarify which instruction you would like');
      return;
    }

  }

  // Intent to jump to a specific ingredient
  function ingredientJump (agent) {

    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);
    const stepNum = agent.parameters.stepNum; // add a try catch here!!!
    var num = 0;

    return dialogflowAgentDoc.get()
      .then(doc => {
        if (!doc.exists) {
          agent.add('This recipe does not exist. Please try another recipe.');
        }
        else {
          ingredientLength = doc.data().numbers.ingredients;

          if (stepNum != "")
          {
            switch (stepNum) {
              case 'first':
                num = 1;
                break;
              case 'second':
                num = 2;
                break;
              case 'third':
                num = 3;
                break;
              case 'fourth':
                num = 4;
                break;
              case 'fifth':
                num = 5;
                break;
              case 'sixth':
                num = 6;
                break;
              case 'seventh':
                num = 7;
                break;
              case 'eighth':
                num = 8;
                break;
              case 'ninth':
                num = 9;
                break;
              case 'tenth':
                num = 10;
                break;
              case 'last':
                num = ingredientLength;
                break;
              case 'next':
                ingredientCount++;
                num = ingredientCount;
                break;
              case 'repeat':
                num = ingredientCount;
                break;
              case 'back':
                if (ingredientCount == 1) {
                  num = 1;
                }
                else {
                  ingredientCount--;
                  num = ingredientCount;
                }
                break;
            }

            if (num > ingredientLength) {
              agent.add('The recipe only has ' + ingredientLength + ' ingredients.');
              agent.add('Please clarify which ingredient you would like');
            }
            else if (num == 0)
            {
              agent.add("That is not a valid ingredient. Please clarify which ingredient you would like.");
            }
            else {
              var key = 'i' + num;
              agent.add(doc.data().ingredient[key]);
              ingredientCount = num;
              agent.add('Next?');
            }
          }
          else {
            agent.add('The recipe only has ' + ingredientLength + ' ingredients.');
            agent.add('Please clarify which ingredient you would like');
            return;
          }
        }
        return Promise.resolve('Read complete');
      }).catch(() => {
        agent.add('No data for recipe found. Please try another recipe.');
      });
  }

  // Intent to edit the number of servings in a recipe
  function changeServe (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);
    const serveNum = agent.parameters.serveNum;

    if (serveNum) // Error checking to make sure that this parameter has a value
    {
      return db.runTransaction(t => {
        t.update(dialogflowAgentRef, {servings: `${serveNum} servings`});     // Key value pair that overwrites existing key value pair in database
        return Promise.resolve('Write complete');
      }).then(doc => {
        agent.add(`Changed recipe to make ${serveNum} servings.`);
        agent.add('What would you like to do next?');
      }).catch(err => {
        console.log(`Error writing to Firestore: ${err}`);
        agent.add(`Failed to change recipe to make ${serveNum} servings.`);
        agent.add('What would you like to do next?');
      });
    }
    else
    {
      agent.add("Please try again and specify the number of servings you would like to change this recipe to.");
    }
  }

  // Intent to change the time a recipe takes
  function changeTime (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);
    const recipeTime = agent.parameters.recipeTime;

    if (recipeTime)
    {
      return db.runTransaction(t => {
        t.update(dialogflowAgentRef, {time: recipeTime});
        return Promise.resolve('Write complete');
      }).then(doc => {
        agent.add(`Changed recipe to take ${recipeTime}.`);
        agent.add('What would you like to do next?');
      }).catch(err => {
        console.log(`Error writing to Firestore: ${err}`);
        agent.add(`Failed to change recipe to take ${recipeTime}.`);
        agent.add('What would you like to do next?');
      });
    }
    else
    {
      agent.add("Please try again and specify the amount of time this recipe will take.");
    }
  }

  // Intent to change ingredient
  function changeIngredient (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);

    try {
      const stepNum = agent.parameters.stepNum;
      const ingredientText = agent.parameters.any;
      var num = 1;

      if (ingredientText)
      {
        switch (stepNum) {
          case 'first':
            num = 1;
            break;
          case 'second':
            num = 2;
            break;
          case 'third':
            num = 3;
            break;
          case 'fourth':
            num = 4;
            break;
          case 'fifth':
            num = 5;
            break;
          case 'sixth':
            num = 6;
            break;
          case 'seventh':
            num = 7;
            break;
          case 'eighth':
            num = 8;
            break;
          case 'ninth':
            num = 9;
            break;
          case 'tenth':
            num = 10;
            break;
        }

        var key = ('ingredient.i' + num);

        return db.runTransaction(t => {
          t.update(dialogflowAgentRef, {[key]: ingredientText});
          return Promise.resolve('Write complete');
        }).then(doc => {
          agent.add(`Changed ${stepNum} ingredient to ${ingredientText}.`);
          agent.add('What would you like to do next?');
        }).catch(err => {
          console.log(`Error writing to Firestore: ${err}`);
          agent.add(`Failed to change the ${stepNum} ingredient.`);
          agent.add('What would you like to do next?');
        });
      }
      else
      {
        agent.add('Please try again and specify what you would like to change the ingredient to.');
        return;
      }
    }
    catch (err) {
      agent.add('Please try again and specify which number ingredient you would like to change.');
    }
  }

  // Intent to launch instruction changing procedure
  function changeInstruction (agent) {

    const dialogflowAgentDoc = db.collection('recipes').doc(recipe);
    const stepNum = agent.parameters.stepNum;

    if (stepNum)
    {
      switch (stepNum) {
        case 'first':
          changeNum = 1;
          break;
        case 'second':
          changeNum = 2;
          break;
        case 'third':
          changeNum = 3;
          break;
        case 'fourth':
          changeNum = 4;
          break;
        case 'fifth':
          changeNum = 5;
          break;
        case 'sixth':
          changeNum = 6;
          break;
        case 'seventh':
          changeNum = 7;
          break;
        case 'eighth':
          changeNum = 8;
          break;
        case 'ninth':
          changeNum = 9;
          break;
        case 'tenth':
          changeNum = 10;
          break;
      }
    }
    else {
        agent.add('Please try again and specify the instruction you would like to make edits to.');
        return;
    }

    if (changeNum){

      if (changeNum > instructionLength) {
        agent.add('The recipe only has ' + instructionLength + ' instructions.');
        agent.add('Please clarify which instruction you would like');
        return;
      }

      return dialogflowAgentDoc.get()
        .then(doc => {
          if (!doc.exists) {
            agent.add('No data for recipe found. Please try another recipe.');
          } else {
            agent.add(`Changing instruction ${changeNum}:`);
            agent.add('Please say "Ingredient is" followed by the ingredient. Say "Ingredient is none" if this instruction does not have an ingredient or "Ingredient is same" if you do not want the ingredient to change.');
          }
          return Promise.resolve('Read complete');
        }).catch(() => {
          agent.add('No data for recipe found. Please try another recipe.');
        });
    }
    else
    {
      agent.add('Please try again and specify the instruction you would like to make edits to.');
      return;
    }
  }

  // Function to change the ingredient field in the instruction
  function changeInstructionIngredient (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);
    const ingredientText = agent.parameters.any;

    if (ingredientText === "")
    {
      agent.add('Please start again and specify what you would like to change the ingredient to.');
      return;
    }
    else {
      var keyI = ('instruction.step' + changeNum + '.ingredient');
      var keyN = ('instruction.step' + changeNum + '.number');

      switch (ingredientText) {
        case 'none':
          return db.runTransaction(t => {
            t.update(dialogflowAgentRef, {[keyI]: '', [keyN]: 0});
            ingredFood = '';
            ingredNum = 0;
            return Promise.resolve('Write complete');
          }).then(doc => {
            agent.add('Ingredient cleared.');
            agent.add('What is the procedure for this instruction? Please say "Procedure is" followed by the instruction procedure.');
          }).catch(err => {
            console.log(`Error writing to Firestore: ${err}`);
            agent.add(`Failed to clear the ingredient in step ${changeNum}.`);
            agent.add('Please start again.');
          });

        case 'same':
          return dialogflowAgentRef.get()
            .then(doc => {
              if (!doc.exists) {
                agent.add('This recipe cannot be found. Please try again.');
              } else {
                var key = ('step' + changeNum);
                ingredFood = doc.data().instruction[key].ingredient;
                ingredNum = doc.data().instruction[key].number;
                agent.add('Ingredient not changed.');
                agent.add('What is the procedure for this instruction? Please say "Procedure is" followed by the instruction procedure.');
              }
              return Promise.resolve('Read complete');
            }).catch(() => {
              agent.add('There was an error updating this instruction.');
              agent.add('Please start again.');
            });

        default:
          return db.runTransaction(t => {
            t.update(dialogflowAgentRef, {[keyI]: ingredientText});
            ingredFood = ingredientText;
            return Promise.resolve('Write complete');
          }).then(doc => {
            agent.add(`Changed ingredient to "${ingredientText}"".`);
            agent.add('What number ingredient does this instruction correspond to? Please say "Number is" followed by the number.');
            // Please say "Number is new" if this is a new ingredient. You can say "Number is same" if this number has not changed.
          }).catch(err => {
            console.log(`Error writing to Firestore: ${err}`);
            agent.add(`Failed to change the ingredient in step ${changeNum}.`);
            agent.add('Please start again.');
          });
        }
    }
  }

  // Function to change the number of ingredient that the instruction corresponds to by changing the number field of step
  function changeInstructionNumber (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);
    const ingredientNum = agent.parameters.number;
    var keyN = ('instruction.step' + changeNum + '.number');

    if (ingredientNum)
    {
      if (ingredientNum > ingredientLength)
      {
        agent.add('The recipe only has ' + ingredientLength + ' ingredients.');
        agent.add('Please start again and clarify which ingredient you would like');
        return;
      }
      else {
        return db.runTransaction(t => {
          t.update(dialogflowAgentRef, {[keyN]: ingredientNum});
          ingredNum = ingredientNum;
          return Promise.resolve('Write complete');
        }).then(doc => {
          agent.add(`Changed number to "${ingredientNum}".`);
          agent.add('What is the procedure for this instruction? Please say "Procedure is" followed by the instruction procedure.');
        }).catch(err => {
          console.log(`Error writing to Firestore: ${err}`);
          agent.add(`Failed to change the ingredient number in step ${changeNum}.`);
          agent.add('Please start again.');
        });
      }
    }
    else {
      agent.add('Please start again and specify the number of ingredient that this instruction corresponds to.');
      return;
    }
  }

  // Function to change the procedure field in the instruction
  function changeInstructionProcedure (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);
    const procedureText = agent.parameters.any;
    var keyP = ('instruction.step' + changeNum + '.procedure');

    if (procedureText === "")
    {
      agent.add('Please start again and specify what you would like to change the procedure to.');
      return;
    }
    else {
      if (procedureText == 'same') {
        agent.add('Procedure is not changed.');
        agent.add('What is the weight of the ingredient to be added in this procedure? Please say "Weight is" followed by the weight. Please say "Weight is none" if there is no weight involved in this procedure.');
      }
      else {
        return db.runTransaction(t => {
          t.update(dialogflowAgentRef, {[keyP]: procedureText});
          return Promise.resolve('Write complete');
        }).then(doc => {
          agent.add(`Changed procedure to "${procedureText}".`);
          agent.add('What is the weight of the ingredient to be added in this procedure? Please say "Weight is" followed by the numerical value of the weight. Please say "Weight is none" if there is no weight involved in this procedure, or "Weight is same" if you do not want to change it.');
        }).catch(err => {
          console.log(`Error writing to Firestore: ${err}`);
          agent.add(`Failed to change the procedure in step ${changeNum}.`);
          agent.add('Please start again.');
        });
      }
    }
  }

  // Function to change the weight field in the instruction
  function changeInstructionWeight (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);
    const weightNum = agent.parameters.number;
    var num = weightNum.toString(10);
    const weightText = agent.parameters.changeValue;
    var keyW = ('instruction.step' + changeNum + '.weight');
    var keyU = ('instruction.step' + changeNum + '.unit');

    if (weightText === "" && !weightNum)
    {
      agent.add('Please start again and specify what you would like the weight to change to.');
      return;
    }
    else {
      switch (weightText) {
        case 'none':
          return db.runTransaction(t => {
            t.update(dialogflowAgentRef, {[keyW]: '', [keyU]: ''});
            ingredWeight = '';
            return Promise.resolve('Write complete');
          }).then(doc => {
            agent.add('Weight cleared.');
            agent.add('Instruction change completed. If you would like to update the ingredient that corresponds to this instruction, please say "Update ingredient."');
          }).catch(err => {
            console.log(`Error writing to Firestore: ${err}`);
            agent.add(`Failed to clear the weight in step ${changeNum}.`);
            agent.add('Please start again.');
          });

        case 'same':
          return dialogflowAgentRef.get()
            .then(doc => {
              if (!doc.exists) {
                agent.add('This recipe cannot be found. Please try again.');
              } else {
                var key = ('step' + changeNum);
                ingredWeight = doc.data().instruction[key].weight;
                agent.add('Weight not changed.');
                agent.add('Instruction change completed. If you would like to update the ingredient that corresponds to this instruction, please say "Update ingredient."');
              }
              return Promise.resolve('Read complete');
            }).catch(() => {
              agent.add('There was an error updating this instruction.');
              agent.add('Please start again.');
            });

        default:
          return db.runTransaction(t => {
            t.update(dialogflowAgentRef, {[keyW]: num, [keyU]: 'g'});
            ingredWeight = num;
            return Promise.resolve('Write complete');
          }).then(doc => {
            agent.add(`Changed weight to "${weightNum}".`);
            agent.add('Instruction change completed. If you would like to update the ingredient that corresponds to this instruction, please say "Update ingredient."');
          }).catch(err => {
            console.log(`Error writing to Firestore: ${err}`);
            agent.add(`Failed to change the weight in step ${changeNum}.`);
            agent.add('Please start again.');
          });
        }
    }
  }

  // Function to update ingredient corresponding to instruction after instruction change
  function changeInstructionUpdate (agent) {

    const dialogflowAgentRef = db.collection('recipes').doc(recipe);

    var key = ('ingredient.i' + ingredNum);
    var text = (ingredWeight + ' g of ' + ingredFood);

    return db.runTransaction(t => {
      t.update(dialogflowAgentRef, {[key]: text});
      return Promise.resolve('Write complete');
    }).then(doc => {
      agent.add(`Updated ingredient to "${text}".`);
      agent.add('What would you like to do next?');
    }).catch(err => {
      console.log(`Error writing to Firestore: ${err}`);
      agent.add(`Failed to update the ingredient corresponding to instruction ${changeNum}.`);
      agent.add('Please start again.');
    });
  }

  // Map from Dialogflow intent names to functions to be run when the intent is matched
  let intentMap = new Map();
  intentMap.set('ReadFromFirestore', readFromDb);
  intentMap.set('WriteToFirestore', writeToDb);
  intentMap.set('recipeStart', recipeStart);
  intentMap.set('recipeServe', recipeServe);
  intentMap.set('recipeTime', recipeTime);
  intentMap.set('instructionStart', instructionStart);
  intentMap.set('ingredientStart', ingredientStart);
  intentMap.set('instructionJump', instructionJump);
  intentMap.set('ingredientJump', ingredientJump);
  intentMap.set('changeServe', changeServe);
  intentMap.set('changeTime', changeTime);
  intentMap.set('changeIngredient', changeIngredient);
  intentMap.set('changeInstruction', changeInstruction);
  intentMap.set('changeInstruction - Ingredient', changeInstructionIngredient);
  intentMap.set('changeInstruction - Number', changeInstructionNumber);
  intentMap.set('changeInstruction - Procedure', changeInstructionProcedure);
  intentMap.set('changeInstruction - Weight', changeInstructionWeight);
  intentMap.set('changeInstruction - Update', changeInstructionUpdate);
  agent.handleRequest(intentMap);
});
