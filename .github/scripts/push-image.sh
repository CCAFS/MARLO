#!/usr/bin/env bash
# Pushes a locally built image to its ECR repository and records its immutable digest as the step output
# `digest`. The registry login uses a private Docker configuration removed at exit (the token is never
# stored in ~/.docker). The tag is for humans only (deploys use the digest) and cannot collide with the
# Jenkins integer tags.
# Inputs (environment): REPOSITORY, LOCAL, TAG, AWS_REGION and the step-scoped temporary credentials.
set -euo pipefail
: "${REPOSITORY:?}" "${LOCAL:?}" "${TAG:?}" "${AWS_REGION:?}"
[[ "$REPOSITORY" =~ ^[a-z0-9][a-z0-9._/-]{0,255}$ ]] || { echo "::error::invalid repository name"; exit 1; }
[[ "$TAG" =~ ^gha-[0-9]+-[0-9]+$ ]] || { echo "::error::invalid tag"; exit 1; }

account="$(aws sts get-caller-identity --query Account --output text)"
[[ "$account" =~ ^[0-9]{12}$ ]] || { echo "::error::could not derive the account (fail closed)"; exit 1; }
echo "::add-mask::$account"
registry="${account}.dkr.ecr.${AWS_REGION}.amazonaws.com"

DOCKER_CONFIG="$(mktemp -d)"
export DOCKER_CONFIG
trap 'rm -rf "$DOCKER_CONFIG"' EXIT
aws ecr get-login-password | docker login --username AWS --password-stdin "$registry" > /dev/null

image="${registry}/${REPOSITORY}:${TAG}"
docker tag "$LOCAL" "$image"
docker push --quiet "$image" > /dev/null

# The digest comes from the image's RepoDigests entry for this repository (not scraped from push output).
digest=""
while IFS= read -r line; do
  case "$line" in "${registry}/${REPOSITORY}@"*) digest="${line#"${registry}/${REPOSITORY}@"}"; break ;; esac
done < <(docker inspect --format '{{range .RepoDigests}}{{println .}}{{end}}' "$image")
[[ "$digest" =~ ^sha256:[0-9a-f]{64}$ ]] || { echo "::error::could not capture the digest (fail closed)"; exit 1; }

echo "digest=$digest" >> "$GITHUB_OUTPUT"
echo "pushed ${REPOSITORY}@${digest} (tag ${TAG})"
echo "- \`${REPOSITORY}@${digest}\`" >> "$GITHUB_STEP_SUMMARY"
