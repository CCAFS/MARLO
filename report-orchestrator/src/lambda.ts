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
    serverlessHandler = serverlessExpress({ app });
  }
  return serverlessHandler(event, context, callback);
};
