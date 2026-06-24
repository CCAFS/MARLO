import dotenv from 'dotenv';
import serverlessExpress from '@codegenie/serverless-express';
import { Handler } from 'aws-lambda';

import { createApp } from './app';
import { loadConfig } from './config/env';

dotenv.config();

const config = loadConfig();
const app = createApp(config);

let serverlessHandler: Handler | undefined;

export const handler: Handler = async (event, context, callback) => {
  if (!serverlessHandler) {
    serverlessHandler = serverlessExpress({
      app,
      // Lambda Function URL is not binary-safe unless the response is base64-encoded.
      binarySettings: {
        contentTypes: ['application/pdf', 'application/octet-stream'],
      },
    });
  }
  return serverlessHandler(event, context, callback);
};
