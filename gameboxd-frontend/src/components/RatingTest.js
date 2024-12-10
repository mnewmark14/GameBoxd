// src/components/RatingTest.js

import React, { useState } from 'react';
import { Rating, Button, Group, Text } from '@mantine/core';
import { showNotification } from '@mantine/notifications';
import { IconCheck } from '@tabler/icons-react';

const RatingTest = () => {
  const [ratingValue, setRatingValue] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmitRating = () => {
    if (ratingValue === null) {
      showNotification({
        title: 'No Rating Selected',
        message: 'Please select a rating before submitting.',
        color: 'red',
        icon: <IconCheck />,
      });
      return;
    }

    setSubmitting(true);

    // Simulate an API call
    setTimeout(() => {
      showNotification({
        title: 'Rating Submitted',
        message: `You have submitted a rating of ${ratingValue}`,
        color: 'green',
        icon: <IconCheck />,
      });
      setSubmitting(false);
      setRatingValue(null); // Reset the rating
    }, 1000);
  };

  return (
    <Group direction="column" align="center" spacing="md">
      <Text>Rate This Game:</Text>
      <Rating
        value={ratingValue}
        onChange={(value) => setRatingValue(value)}
        fractions={2}
        size="md"
        clearable
        aria-label="Submit Rating"
      />
      <Button onClick={handleSubmitRating} disabled={submitting} loading={submitting}>
        Submit Rating
      </Button>
    </Group>
  );
};

export default RatingTest;
