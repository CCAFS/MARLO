FROM public.ecr.aws/lambda/nodejs:20

# esbuild bundle (see npm run package:lambda)
COPY lambda-package/lambda.js ${LAMBDA_TASK_ROOT}/lambda.js

CMD ["lambda.handler"]
